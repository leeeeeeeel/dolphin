package network.netMsg;

import java.io.Serializable;

public class NetMsg implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private byte messageType;
	
	public static final class MessageType {
		public static final byte none = 0;
		
		public static final byte greeting = 1;
		public static final byte onGreeting = 2;
		
		public static final byte statusUpdate = 3;
	}
	
	private String username;
	private String token;

	public NetMsg() { 
		setMessageType(MessageType.none);
	}

	public byte getMessageType() {
		return messageType;
	}
	public void setMessageType(byte messageType) {
		this.messageType = messageType;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}