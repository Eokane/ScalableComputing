package server;

/*To send a chat message the client sends the following:
CHAT: [ROOM_REF]
JOIN_ID: [integer identifying client to server]
CLIENT_NAME: [string identifying client user]
MESSAGE: [string terminated with '\n\n']

The server should send the following message to every client presently connected to the chat room:
CHAT: [ROOM_REF]
CLIENT_NAME: [string identifying client user]
MESSAGE: [string terminated with '\n\n']
*/



public class ChatRequest {

	String chatRoomRef = "";
	String clientJoinID = "";
	String clientName = "";
	String message = "";

	public String getChatRoomRef() {
		return chatRoomRef;
	}

	public void setChatRoomRef(String chatRoomRef) {
		this.chatRoomRef = chatRoomRef;
	}

	public String getClientJoinID() {
		return clientJoinID;
	}

	public void setclientJoinID(String clientJoinID) {
		this.clientJoinID = clientJoinID;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ChatRequest [ChatroomReference=" + chatRoomRef + ", clientJoin_ID=" + clientJoinID + ", Client_Name="
				+ clientName + ", Message=" + message + "]";
	}

}
