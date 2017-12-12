package server;

import java.util.HashMap;
import java.util.Map;

public class Chatrooms {

	Map<String, ChatRoom> chatRoomMap = new HashMap<String, ChatRoom>();

	public Map<String, ChatRoom> getChatRoomMap() {
		return chatRoomMap;
	}

	public void setChatRoomMap(Map<String, ChatRoom> chatRoomMap) {
		this.chatRoomMap = chatRoomMap;
	}

}
