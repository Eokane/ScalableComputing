package server;

/*Joining a chat room is initiated by a client by sending the following message to a chat server.
JOIN_CHATROOM: [chatroom name]
CLIENT_IP: [IP Address of client if UDP | 0 if TCP]
PORT: [port number of client if UDP | 0 if TCP]
CLIENT_NAME: [string Handle to identifier client user]*/

//Creating Variables ad described in question for matcher function in main ChatServer


public class JoinChatroomRequest {

	String chatRoomName = "";
	String clientIP = "";
	String port = "";
	String clientName = "";

	public String getChatRoomName() {
		return chatRoomName;
	}

	public void setChatRoomName(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public String toString() {
		return "JoinChatroomRequest [Chatroom_Name=" + chatRoomName + ", Client_IP=" + clientIP 
										+ ", Port=" + port
										+ ", Client_Name=" + clientName + "]";
	}

}
