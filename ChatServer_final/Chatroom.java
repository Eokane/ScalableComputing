package server;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoom {

	String roomName = "";
	String roomId = "";
	Map<String, Client> clientMap = new HashMap<String, Client>();

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Map<String, Client> getClientMap() {
		return clientMap;
	}

	public void setClientMap(Map<String, Client> clientMap) {
		this.clientMap = clientMap;
	}

	@Override
	public String toString() {
		return "ChatRoom [room_REF=" + roomName + ", Room_ID=" + roomId + ", Client_Database=" + clientMap + "]";
	}

}
