package server;

/*JOINED_CHATROOM: [chatroom name]
		  SERVER_IP: [IP address of chat room]
		  PORT: [port number of chat room]
		  ROOM_REF: [integer that uniquely identifies chat room on server]
		  JOIN_ID: [integer that uniquely identifies client joining]
*/

public class JoinChatroomResponse {

	String chatRoomName = "";
	String serverIPAddress = "";
	int portNumber = 0;
	int chatRoomID = 0;
	int clientJoinID = 0;

	public String getChatRoomName() {
		return chatRoomName;
	}

	public void setChatRoomName(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}

	public String getServerIPAddress() {
		return serverIPAddress;
	}

	public void setServerIPAddress(String serverIPAddress) {
		this.serverIPAddress = serverIPAddress;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public int getChatRoomID() {
		return chatRoomID;
	}

	public void setChatRoomID(int chatRoomID) {
		this.chatRoomID = chatRoomID;
	}

	public int getClientJoinID() {
		return clientJoinID;
	}

	public void setClientJoinID(int clientJoinID) {
		this.clientJoinID = clientJoinID;
	}

}
