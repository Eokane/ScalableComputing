package server;

/*A client leaves a chat room by sending the following message to the chat server:
	  LEAVE_CHATROOM: [ROOM_REF]
	  JOIN_ID: [integer previously provided by server on join]
	  CLIENT_NAME: [string Handle to identifier client user]
	
The server responds with the following message:
	  LEFT_CHATROOM: [ROOM_REF]
	  JOIN_ID: [integer previously provided by server on join]
*/
public class LeaveChatroomRequest {

	String chatRoomRef = "";
	int clientJoinID = 0;
	String clientName = "";

	public int getClientJoinID() {
		return clientJoinID;
	}

	public void setClientJoinID(int clientJoinID) {
		this.clientJoinID = clientJoinID;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getChatRoomRef() {
		return chatRoomRef;
	}

	public void setChatRoomRef(String chatRoomRef) {
		this.chatRoomRef = chatRoomRef;
	}

	@Override
	public String toString() {
		return "LeaveChatroom [Leave_Chatroom=" + chatRoomRef + ", Join_ID_ofclient=" + clientJoinID + ", Client_Name="
				+ clientName + "]";
	}

}
