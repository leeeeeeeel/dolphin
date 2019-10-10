package visual;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.Color;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.Data;
import network.Network;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class Client {

	private JFrame frmDolphin;
	
	private File historyFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client window = new Client();
					window.frmDolphin.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Client() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDolphin = new JFrame();
		frmDolphin.setTitle("dolphin");
		frmDolphin.setBounds(100, 100, 500, 600);
		frmDolphin.setMinimumSize(new Dimension(516, 638));
		frmDolphin.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frmDolphin.setVisible(true);
		frmDolphin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDolphin.getContentPane().setLayout(new CardLayout());

		initializeEntry();
		initializeMaster();
	}
	
	private void initializeEntry() {
		JPanel panEntry = new JPanel();
		panEntry.setBackground(new Color(20, 100, 152, 255));
		panEntry.setLayout(new BorderLayout(0, 0));
		
		Box entryBox = new Box(BoxLayout.Y_AXIS);
		
		JPanel panAuthentication = new JPanel();
		panAuthentication.setBackground(new Color(15, 75, 114, 255));
		panAuthentication.setPreferredSize(new Dimension(500, 600));
		panAuthentication.setMaximumSize(new Dimension(500, 600));
		panAuthentication.setMinimumSize(new Dimension(500, 600));
		panAuthentication.setLayout(null);
		
		JLabel lblDolphin = new JLabel("Dolphin");
		lblDolphin.setFont(new Font("Arial", Font.BOLD, 40));
		lblDolphin.setForeground(Color.WHITE);
		lblDolphin.setHorizontalAlignment(SwingConstants.CENTER);
		lblDolphin.setBounds(0, 15, 500, 70);
		
		JLabel lblUsername = new JLabel("username");
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsername.setForeground(Color.WHITE);
		lblUsername.setFont(new Font("Arial", Font.BOLD, 20));
		lblUsername.setBounds(0, 120, 500, 30);
		
		JTextField txtUsername = new JTextField();
		txtUsername.setBounds(100, 160, 300, 30);
		txtUsername.setColumns(10);
		
		JLabel lblDataFile = new JLabel("data file");
		lblDataFile.setForeground(Color.WHITE);
		lblDataFile.setFont(new Font("Arial", Font.BOLD, 20));
		lblDataFile.setHorizontalAlignment(SwingConstants.CENTER);
		lblDataFile.setBounds(0, 220, 500, 30);
		
		JButton btnDataFile = new JButton("select data file");
		btnDataFile.setForeground(Color.BLACK);
		btnDataFile.setFont(new Font("Arial", Font.BOLD, 15));
		btnDataFile.setBounds(150, 260, 200, 30);

		JLabel lblSelectedFile = new JLabel("none");
		lblSelectedFile.setForeground(Color.WHITE);
		lblSelectedFile.setHorizontalAlignment(SwingConstants.CENTER);
		lblSelectedFile.setFont(new Font("Arial", Font.PLAIN, 12));
		lblSelectedFile.setBounds(150, 300, 200, 20);
		
		JButton btnAuthentication = new JButton("authenticate");
		btnAuthentication.setFont(new Font("Arial", Font.BOLD, 40));
		btnAuthentication.setForeground(new Color(0, 0, 0));
		btnAuthentication.setBounds(100, 400, 300, 100);

		panAuthentication.add(lblDolphin);
		panAuthentication.add(lblUsername);
		panAuthentication.add(txtUsername);
		panAuthentication.add(lblDataFile);
		panAuthentication.add(btnDataFile);
		panAuthentication.add(lblSelectedFile);
		panAuthentication.add(btnAuthentication);
		
		entryBox.add(Box.createVerticalGlue());
		entryBox.add(panAuthentication);
		entryBox.add(Box.createVerticalGlue());
		
		panEntry.add(entryBox);
		
		frmDolphin.getContentPane().add(panEntry, "entry");
		
		// Events
		btnDataFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("select history file");
				fc.setFileFilter(new FileNameExtensionFilter("dolphin files", "dolphin"));
				
		        int returnVal = fc.showOpenDialog(frmDolphin);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		        	Data.loadHistory(fc.getSelectedFile());
		            lblSelectedFile.setText(historyFile.getName());
		        }
			}
		});
		btnAuthentication.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				try {
					Network.start(txtUsername.getText());
				} catch (IOException e) {
					// TODO
					e.printStackTrace();
				}

				changeToMainPanel();
			}
		});
	}
	
	private void initializeMaster() {
		JPanel panMaster = new JPanel();
		panMaster.setBackground(new Color(20, 100, 152, 255));
		panMaster.setLayout(new BorderLayout(0, 0));
		
		Box box = new Box(BoxLayout.Y_AXIS);
		
		JPanel panMain = new JPanel();
		panMain.setPreferredSize(new Dimension(800, 600));
		panMain.setMaximumSize(new Dimension(800, 600));
		panMain.setMinimumSize(new Dimension(800, 600));
		panMain.setLayout(null);
		
		JPanel panMessages = new JPanel();
		panMessages.setBounds(250, 0, 550, 600);
		panMessages.setLayout(null);
		
		JPanel panMessageFlow = new JPanel();
		panMessageFlow.setBounds(0, 0, 550, 550);
		panMessageFlow.setBackground(new Color(15, 75, 114, 255));
		panMessageFlow.setLayout(null);
		
		JPanel panSendMessage = new JPanel();
		panSendMessage.setBounds(0, 550, 550, 50);
		panSendMessage.setBackground(new Color(10, 50, 76, 255));
		panSendMessage.setLayout(null);
		
		JTextField txtMessage = new JTextField();
		txtMessage.setBounds(10, 10, 490, 30);
		
		JButton btnMessage = new JButton();
		btnMessage.setBounds(510, 10, 30, 30);
		
		panSendMessage.add(txtMessage);
		panSendMessage.add(btnMessage);
		
		panMessages.add(panMessageFlow);
		panMessages.add(panSendMessage);
		
		JPanel panAside = new JPanel();
		panAside.setBounds(0, 0, 250, 600);
		panAside.setLayout(null);
		
		JPanel panCommands = new JPanel();
		panCommands.setBounds(0, 0, 250, 65);
		panCommands.setBackground(new Color(5, 25, 38, 255));
		panCommands.setLayout(null);
		
		JLabel lblUsername = new JLabel();
		lblUsername.setBounds(10, 5, 200, 40);
		lblUsername.setForeground(new Color(255, 255, 255));
		lblUsername.setFont(new Font("Arial", Font.BOLD, 30));
		lblUsername.setVerticalAlignment(SwingConstants.TOP);
		lblUsername.setText("xxxxxxxxxxxxxxxxx");
		
		JLabel lblAddress = new JLabel();
		lblAddress.setBounds(10, 40, 200, 20);
		lblAddress.setForeground(new Color(20, 100, 152, 255));
		lblAddress.setFont(new Font("Arial", Font.ITALIC, 15));
		lblAddress.setVerticalAlignment(SwingConstants.TOP);
		lblAddress.setText("xxx.xxx.xxx.xxx:xxxxx");
		
		JButton btnConfigurations = new JButton("");
		btnConfigurations.setBounds(212, 20, 25, 25);
		
		JPanel panStatus = new JPanel();
		
		panCommands.add(lblUsername);
		panCommands.add(lblAddress);
		panCommands.add(btnConfigurations);
		
		JPanel panChats = new JPanel();
		panChats.setBounds(0, 65, 250, 535);
		panChats.setBackground(new Color(10, 50, 76, 255));
		panChats.setLayout(null);
		
		panAside.add(panCommands);
		panAside.add(panChats);
		
		panMain.add(panMessages);
		panMain.add(panAside);
		
		box.add(Box.createVerticalGlue());
		box.add(panMain);
		box.add(Box.createVerticalGlue());
		
		panMaster.add(box);
		
		frmDolphin.getContentPane().add(panMaster, "master");
	}
	
	// REMOVE
	private void changeToMainPanel() {
		frmDolphin.setMinimumSize(new Dimension(816, 638));
		
		((CardLayout) frmDolphin.getContentPane().getLayout()).show(frmDolphin.getContentPane(), "master");
	}
}
