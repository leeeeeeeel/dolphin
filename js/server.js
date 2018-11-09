var express = require('express');
var app = express();
var http = require('http').Server(app);
var cors = require('cors');
var sha256 = require('js-sha256');
var bodyParser = require('body-parser');
var sql = require('mssql');
var outrosql = require('mssql');
var io = require('socket.io')(http);

var clients = [];
var rooms = [{
  name: "global",
  pass: sha256("")
}];
var roomClient = [];

app.use(cors());


app.use(bodyParser.urlencoded({
  extended: true
}));
app.use(bodyParser.json());
const rota = express.Router();
app.use('/', rota);

app.get('/', function(req, res) {
  res.send('Servidor Ativo');
});
http.listen(3000, async function() {
  console.log("Servidor rodando na porta: 3000");
  await sql.connect("Server=regulus.cotuca.unicamp.br;Database=PR118175;User Id=PR118175;Password=75SENHA97");
  const request = new sql.Request();
  request.query("select nome as username from Usuario; select nome as nome, senha as senha from sala; select usuario as usuario, sala as sala from salausuario;").then(result => {
      for (var i = 0; i < result.recordsets[0].length; i++) {
        clients[i] = result.recordsets[0][i].username;
      }

      for (var i = 1; i < result.recordsets[1].length; i++) {
        rooms[i] = {
          name: result.recordsets[1][i].nome,
          pass: result.recordsets[1][i].senha
        };
      }

      for (var i = 0; i < result.recordsets[2].length; i++) {
        roomClient[i] = {
          name: clients[result.recordsets[2][i].usuario - 1],
          room: rooms[result.recordsets[2][i].sala - 1].name
        }
      }
    }

  ).catch(result => {
    console.log(result);
  });
});

io.on("connection", function(client) {
  console.log('usuario conectado');
  for (var i = 0; i < rooms.length; i++)
    client.emit("created", {
      name: rooms[i].name,
      pass: rooms[i].pass != sha256("")
    });

  client.on("login", function(username) {
    clients[client.id] = username;
  });

  client.on("join", function(data) {
    var roomID = roomExists(data.name);
    if (roomID != -1) {
      if (rooms[roomID].pass == sha256(data.pass)) {
        client.join(data.name);
        client.emit("join", data.name);
        console.log(clients[client.id] + " entrou na sala " + data.name);
        client.emit("correctPass", {
          response: true,
          id: data.name
        });
        client.emit("joined", {
          msg: "Você entrou na sala " + data.name,
          room: data.name
        });
        client.broadcast.to(data.name).emit("joined", {
          msg: clients[client.id] + "entrou na sala " + data.name,
          room: data.name
        });
        roomClient[roomClient.length] = {
          room: data.name,
          name: clients[client.id]
        };
        if (data.status) {
          userToRoom({
            user: clients[client.id],
            room: data.name
          });
        }


      } else
        client.emit("correctPass", {
          response: false,
          id: data.name
        });
    }
  });
  client.on("msg", function(data) {
    if (roomExists(data.room) != -1) {
      client.broadcast.to(data.room).emit("msg", data);
      const request = new sql.Request();
      request.query(`SELECT Sid AS salaId FROM Sala WHERE Sala.nome = '${data.room}'`).then(
        result => {
          request.query(`INSERT INTO Mensagem(sala, conteudo) VALUES ('${result.recordset[0].salaId}', '${data.msg}')`);
        }
      ).catch(result => {
        console.log(result)
      })
    }

  });
  client.on("create", function(data) {
    if (roomExists(data.name) == -1) {
      rooms[rooms.length] = {
        name: data.name,
        pass: sha256(data.pass)
      };
      console.log(clients[client.id] + "criou a sala" + data.name);
      const request = new sql.Request();
      request.query(`INSERT INTO Sala(nome, senha) VALUES('${data.name}','${sha256(data.pass)}')`).then().catch(result => console.log(result));
      client.join(data.name);
      client.emit("create", data.name);
      if (data.status) {
        userToRoom({
          user: clients[client.id],
          room: data.name
        });
      }

      roomClient[roomClient.length] = {
        room: data.name,
        name: clients[client.id]
      };

      if (data.pass == sha256(""))
        client.broadcast.emit("created", {
          user: clients[client.id],
          name: data.name,
          pass: false
        });
      else
        client.broadcast.emit("created", {
          user: clients[client.id],
          name: data.name,
          pass: true
        });
    } else
      client.emit("roomExist", true);
  });

  client.on("logar", function(data) {
    const name = data.name;
    const pass = sha256(data.pass);
    const request = new sql.Request();
    request.query(`SELECT CAST(CASE WHEN EXISTS(SELECT * FROM Usuario WHERE nome = '${name}' AND senha = '${pass}') THEN 1 ELSE 0 END AS BIT) AS re`).then(
      async result => {
        if (result.recordset[0].re == true) {
          console.log(clients[client.id] + " logou como " + name);
          await client.emit("login", {
            status: true,
          });
          for (var i = 0; i < roomClient.length; i++) {

            client.join(roomClient[i].room);
            if (roomClient[i].name == name) {
              client.emit("logar", roomClient[i].room);
            }
          }
          client.emit("fim", true);
        } else {
          client.emit("login", {
            status: false
          });
        }
      }).catch(result => {
      console.log(result)
    })
  });
  client.on("cadastro", function(room) {
    userToRoom({
      user: clients[client.id],
      room: room
    });
    roomClient[roomClient.length] = {
      name: clients[client.id],
      room: room
    };
  });
  client.on("load", function(room) {
    const request = new sql.Request();
    console.log("nome da sala:  " + room);
    request.query(`select id as id from sala where nome = '${room}' `).then(
      result => {
        console.log("result da query:  " + result.recordsets[0].length);
        request.query(`select conteudo as content from Mensagem where sala = ${result.recordset.id}`).then(
          result => {
            console.log(result.recordset);
            console.log(result.recordset.content);
            client.emit("load", {
              room: room,
              msgs: result.recordset
            })
          }
        ).catch(result => console.log(result))
      }
    ).catch(result => console.log(result))

  });
})

function roomExists(room) {
  for (var i = 0; i < rooms.length; i++)
    if (rooms[i].name == room) {
      return i;
    }
  return -1;
}

function userToRoom(data) {
  const request = new sql.Request();
  request.query(`select id as usuario from usuario where nome = '${data.user}';
  				   select id as sala from sala where nome = '${data.room}'`).then(
    result => {
      request.query(`insert into salausuario(sala, usuario) values(${result.recordsets[1][0].sala}, ${result.recordsets[0][0].usuario})`)
        .then(result => console.log("Dados inseridos com sucesso"))
        .catch(result => console.log(result));
    }).catch(result => {
    console.log(result)
  })
}

rota.post('/register', (req, res) => {
  res.header("Access-Control-Allow-Origin", "*");
  const name = req.body.name;
  const age = req.body.age;
  const pass = sha256(req.body.pass);
  const request = new sql.Request();
  request.query(`SELECT CAST(CASE WHEN EXISTS(SELECT * FROM Usuario WHERE nome = '${name}') THEN 1 ELSE 0 END AS BIT) AS re`).then(
    result => {
      console.log("Usuario " + name + " de " + age + " anos registrado");
      if (result.recordset[0].re == true) {
        res.json({
          signup: false
        });
      } else {
        request.query(`INSERT INTO Usuario(nome, idade, senha) VALUES('${name}', '${age}', '${pass}')`);
        res.json({
          signup: true
        });
      }
    }).catch(result => {
    console.log(result)
  })
})