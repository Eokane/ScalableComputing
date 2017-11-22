package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import server.ChatServer;

public class ChatServer implements Runnable {
	private ChatServerSupport clientArray[] = new ChatServerSupport[50];
	private ServerSocket server = null;
	private Thread thread = null;
	private int clientCount = 0;

	
	/*Use Hashmaps to create dataframe of all clients who connect and what chatrooms 
	 they connect to in order to 'organise' joining and leaving.
	*/
	/*All of the chatroom details*/
	Map<String, Integer> chatroomDatabase = new HashMap<String, Integer>();
	/*To hold room name and room ID*/
	static Map<String, String> roomReference = new HashMap<String, String>();
	/*To hold the client ID and the client name*/
	static Map<String, String> clientReference = new HashMap<String, String>();

	boolean stripNewLineChar = false;

	//Request and Response structures taken from question
   /*Get the IP address of the server*/
	static String serverIPAddress = getServerIPAddress();

	//String format of HELO message as seen in protocol
	String heloText = "(?s)HELO(.*)";
	//Response to HELO input
	static String HELO_RESPONSE = "%s\nIP:%s\nPort:%s\nStudentID:%s";
	//Use this to terminate service by sending the following message string to your service via the main port number
	String killService = "KILL_SERVICE";

	//String format for Request to Join chatroom 
	String Join_Chatroom_Request = "(?s)JOIN_CHATROOM:(.*)CLIENT_IP:(.*)PORT:(.*)CLIENT_NAME:(.*)";
	//Sting format for Response to Join chatroom
	String Join_Chatroom_Response = "JOINED_CHATROOM:%s\nSERVER_IP:%s\nPORT:%s\nROOM_REF:%s\nJOIN_ID:%s\n";
	
	//String format for Request to Leave chatroom 
	String Leave_Chatroom_Request = "(?s)LEAVE_CHATROOM:(.*)JOIN_ID:(.*)CLIENT_NAME:(.*)";
	//Sting format for Response to Leave chatroom
	String Leave_Chatroom_Response = "LEFT_CHATROOM:%s\nJOIN_ID:%d\n";

	//String Format for disconnecting from chatroom
	String Disconnect_Chatroom_Request = "(?s)DISCONNECT:(.*)PORT:(.*)CLIENT_NAME:(.*)";

	//Format of chatroom messages
	String Chatroom_Message = "(?s)CHAT:(.*)JOIN_ID:(.*)CLIENT_NAME:(.*)MESSAGE:(.*)";
	String Chatroom_Message_Response= "CHAT:%s\nCLIENT_NAME:%s\nMESSAGE:%s\n\n";

	Client client = null;// new Client();

	int chatRoomCount = 0;

	static AtomicInteger atmInt = new AtomicInteger();

	static ChatRoom chatRoom = new ChatRoom();

	//Details in Chatroom.java
	static Map<Integer, Client> clientMap = new HashMap<Integer, Client>();

	//Details in Chatrooms.java
	static HashMap<String, ChatRoom> chatRoomMap = new HashMap<String, ChatRoom>();

	static Chatrooms chatRooms = new Chatrooms();

	public String getUniqueID() {

		return String.valueOf(System.currentTimeMillis());
	}

	public static String getServerIPAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {

			System.out.print(e.getMessage());
		}
		return null;
	}

	public ChatServer(int port) {
		try {
			System.out.print("Currently binding to port number " + port + ", please wait  ...");
			server = new ServerSocket(port);
			System.out.print("Server has started: " + server);
			start();
		} catch (IOException ioe) {
			System.out.print("Unable to bind to port " + port + ": " + ioe.getMessage());
		}
	}
//Iterating through a thread
	public void run() {
		while (thread != null) {
			try {
				System.out.print("Waiting for a client to join ...");
				addThread(server.accept());
			} catch (IOException ioe) {
				System.out.print("Server accept error: " + ioe);
				stop();
			}
		}
	}
//Creating a thread and then triggering it
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
			System.out.print("Thread started");
		}
	}

	public void stop() {
		if (thread != null) {
			thread.stop();
			thread = null;
		}
	}
//Uses ChatServerSupport.java
	private int findClient(int ID) {
		for (int i = 0; i < clientCount; i++)
			if (clientArray[i].getID() == ID)
				return i;
		return -1;
	}

	public String getNewID() {
		return String.valueOf(atmInt.incrementAndGet());
	}
	/*This takes the full input sting and matches it to the input pattern to decipher variables*/
	//In ChatServerSupport the string is put into one string instead of a few lines
	// That is it is in the format required by protcol
	public synchronized void handle(int ID, String input) throws UnknownHostException {

		System.out.print("Input msg:" + input);
		System.out.print("Input ID:" + ID);
		
     //Need to store ID as a string
		String strID = String.valueOf(ID);
      //remove ID if client sends bye
		if (input.equals(".bye")) {

			clientArray[findClient(ID)].send(".bye");
			remove(ID);

		} else if (input.startsWith(killService)) {
			System.out.print("Exiting service. Kill service initiated");
			System.exit(1);

		} else if (input.matches(heloText)) {

			clientArray[findClient(ID)].send(DecipherHeloMessage(input));

		} else if (input.matches(Join_Chatroom_Request)) {

			JoinChatroomRequest joinRequest = getJoinReqDetails(input);

			String chatroomName = joinRequest.getChatRoomName();

			// Capture Client Details and add to chatroom
			System.out.print("Client:" + strID);

			
            //Organising Hashmaps - checking to see whether the room exists or not. If it doesn't 
		   //The room is created and added
			if (roomReference.containsKey(chatroomName)) {
				System.out.print("The chatroom already exists:" + chatroomName);

				// Old client
				chatRoom = chatRoomMap.get(roomReference.get(chatroomName));

				for (Map.Entry entry : chatRoom.getClientMap().entrySet()) {
					System.out.println(entry.getKey() + ", " + ((Client) entry.getValue()).getId());
				}

			} else {

				System.out.print("Creating chatroom: " + chatroomName +" as it doesn't previously exist");
				chatRoom = new ChatRoom();
				String roomID = getNewID();
				chatRoom.setRoomId(roomID);
				chatRoom.setRoomName(chatroomName);

				roomReference.put(chatroomName, roomID);

				System.out.print("roomReference done");
				clientReference.put(joinRequest.getClientName(), strID);

				System.out.print("clientReference done");

				// clientMap.put(ID, client);

				System.out.print("Added client{} to Chatroom {}" + chatRoom.getRoomId() + ":" + chatroomName);
			}

			client = new Client();
			client.setName(joinRequest.getClientName());
			chatRoom.getClientMap().put(strID, client);
			chatRoomMap.put(chatRoom.getRoomId(), chatRoom);

			System.out.print("after joining:" + chatRoomMap.get("1"));

			// Reply to client
			clientArray[findClient(ID)].send(dechipherJoiningMessage(joinRequest, chatRoom.getRoomId(), strID));

			/*Broadcasting to everyone in the chatroom what the client said*/
			sendNotificationToAll(chatRoom, joinRequest.getClientName(), "joined");

		} else if (input.matches(Leave_Chatroom_Request)) {

			LeaveChatroomRequest leaveRequest = getLeaveCRReqDetails(input);

			String chatroomID = leaveRequest.getChatRoomRef();

			if (chatRoomMap.containsKey(chatroomID)) {

				// Map<Integer, Client> tempClientMapForCheck =
				// chatRoom.getClientMap();

				// Send message to client
				clientArray[findClient(ID)].send(manageLeaveCRMessage(leaveRequest));

				// BroadCast
				// sendMessageToAll(chatRoomTemp, ID, "Client left the
				// chatroom");
				sendNotificationToAll(chatRoomMap.get(chatroomID), leaveRequest.getClientName(), "left");

				ChatRoom chatRoomTemp = chatRoomMap.get(chatroomID);

				// Remove from chatroom and check size of map to ensure it reduces in size
				Map<String, Client> tempClientMap = chatRoomTemp.getClientMap();
				System.out.print("Size of hashmap before removing client:" + tempClientMap.size());
				tempClientMap.remove(strID);
				System.out.print("Size of hashmap after removing client:" + tempClientMap.size());

				chatRoomTemp.setClientMap(tempClientMap);
				chatRoomMap.put(chatroomID, chatRoomTemp);

			} else {
				// Nothing doing

				clientArray[findClient(ID)].send(manageLeaveCRMessage(leaveRequest));

				sendNotificationToAll(chatRoomMap.get(chatroomID), leaveRequest.getClientName(), "left");

			}

		} else if (input.matches(Disconnect_Chatroom_Request)) {
			System.out.print("Disconnecting Client");

			DisconnectRequest disConnReq = getDisconnectReqDetails(input);
			System.out.print(
					"ID of client:" + disConnReq.getClientName() + ":" + clientReference.get(disConnReq.getClientName()));

			System.out.print("Before tree:" + chatRoomMap.keySet());
			
			TreeMap<String, ChatRoom> treemap = new TreeMap<String, ChatRoom>();
			treemap.putAll(chatRoomMap);
			System.out.print("After tree:" + treemap.keySet());
			Iterator<String> charRoomMapItr = treemap.keySet().iterator();

			while (charRoomMapItr.hasNext()) {

				ChatRoom cr = treemap.get(charRoomMapItr.next());

				Map<String, Client> clientItrMap = cr.getClientMap();

				Iterator<String> clientMapItr = cr.getClientMap().keySet().iterator();

				while (clientMapItr.hasNext()) {

					Client clt = clientItrMap.get(clientMapItr.next());

					if (disConnReq.getClientName().equalsIgnoreCase(clt.getName())) {

						sendNotificationToAll(treemap.get(cr.getRoomId()), disConnReq.getClientName(), "left");

					}
				}
			}

			remove(Integer.valueOf(clientReference.get(disConnReq.getClientName())));

		} else if (input.matches(Chatroom_Message)) {

			System.out.print("got a Chatroom message");

			ChatRequest chatReq = getChatroomMessage(input);
			String ChatroomReference = chatReq.getChatRoomRef();

			// is chatroom available?
			if (roomReference.containsValue(ChatroomReference)) {

				String key = "";

				for (Entry<String, String> entry : roomReference.entrySet()) {
					if (entry.getValue().equals(ChatroomReference)) {
						key = entry.getKey();
					}
				}

				System.out.print("Chatroom Name for Chatroom Reference:" + roomReference.get(key));

				for (String keyentry : chatRoomMap.keySet()) {
					System.out.print("Entry:" + keyentry);
				}

				// Get the chatroom
				chatRoom = chatRoomMap.get(roomReference.get(key));

				System.out.print("got Chatroom:" + chatRoom);

				System.out.print("Chatroom Room Details from Hashmap " + chatRoom.toString());

				if (chatRoom.getClientMap().containsKey(strID)) {

					sendMessageToAll(chatRoom, ID, chatReq);

				} else {
					// The client is not part of the Chatroom.
				}

			} else {
				// nothing
			}

			// manageChatroomMessage(input);

		} else {
			for (int i = 0; i < clientCount; i++)
				clientArray[i].send(ID + ": " + input);
		}
	}

	public void sendMessageToAll(ChatRoom chatRoom, int ID, ChatRequest chatReq) {
		Iterator<String> itr = chatRoom.getClientMap().keySet().iterator();

		String response = String.format(Chatroom_Message_Response, chatReq.getChatRoomRef(), chatReq.getClientName(),
				chatReq.getMessage());

		while (itr.hasNext()) {

			String ne = itr.next();
			// System.out.print("NE:" + ne);
			// if (/* ne < clientArray.length && */ ne.equals(ID)) {

			clientArray[findClient(Integer.valueOf(ne))].send(response);
			// }
		}
	}

	public void sendNotificationToAll(ChatRoom chatRoom, String name, String message) {
		System.out.print("sendNotificationToAll:" + chatRoom + ":" + name);
		Iterator<String> itr = chatRoom.getClientMap().keySet().iterator();

		String response = String.format(Chatroom_Message_Response, chatRoom.getRoomId(), name,
				name + " has " + message + " this chatroom");
	
		/*Go through every client who is in the chatroom and push the message from the sender to them*/
		System.out.print("sendNotificationToAll:crossed response");
		int count = 0;
		while (itr.hasNext()) {

			Integer ne = Integer.valueOf(itr.next());
			// System.out.print("NE:" + ne);
			// if (/* ne < clientArray.length && */ ne != ID) {

			System.out.print("Broadcasting to " + ne + response);
			clientArray[findClient(ne)].send(response);
			count++;
			// }
		}

		System.out.print("Completed braodcasting for " + count);

	}

	private String dechipherJoiningMessage(JoinChatroomRequest joinReq, String chatroomID, String id) throws UnknownHostException {

		System.out.print("dechipherJoiningMessage:Request:" + joinReq);
		System.out.print("\ndechipherJoiningMessage:chatroomID:" + chatroomID);
		System.out.print("\ndechipherJoiningMessage:ID:" + id);

		System.out.print("\nID Value:" + id);

		String response = String.format(Join_Chatroom_Response, joinReq.getChatRoomName(), serverIPAddress, server.getLocalPort(),
				chatroomID, id);
		System.out.print("\ndechipherJoiningMessage:Response:" + response);
		return response;
	}

	private synchronized JoinChatroomRequest getJoinReqDetails(String input) {

		System.out.print("ClientDetails:" + input);

		String chatroomName = "";
		String clientIP = "";
		String clientPort = "";
		String clientName = "";

		Matcher Join_Chatroom_Request_matcher = Pattern.compile(Join_Chatroom_Request).matcher(input);

		while (Join_Chatroom_Request_matcher.find()) {

			chatroomName = Join_Chatroom_Request_matcher.group(1);
			clientIP = Join_Chatroom_Request_matcher.group(2);
			clientPort = Join_Chatroom_Request_matcher.group(3);
			clientName = Join_Chatroom_Request_matcher.group(4);
		}

		JoinChatroomRequest joinReq = new JoinChatroomRequest();

		if (stripNewLineChar) {
			chatroomName = stripNewLineChar(chatroomName);
			clientName = stripNewLineChar(clientName);
		}

		joinReq.setClientIP(clientIP);
		joinReq.setChatRoomName(chatroomName.trim());
		joinReq.setClientName(clientName.trim());
		joinReq.setPort(clientPort);

		System.out.print("getClientDetails:" + joinReq.toString());
		return joinReq;
	}

	private synchronized DisconnectRequest getDisconnectReqDetails(String input) {

		System.out.print("getDisconnectReqDetails:" + input);

		String clientIP = "";
		String clientPort = "";
		String clientName = "";

		
		//Dealing with disconnecting Message
		Matcher Join_Chatroom_Request_matcher = Pattern.compile(Disconnect_Chatroom_Request).matcher(input);

		while (Join_Chatroom_Request_matcher.find()) {

			clientIP = Join_Chatroom_Request_matcher.group(1);
			clientPort = Join_Chatroom_Request_matcher.group(2);
			clientName = Join_Chatroom_Request_matcher.group(3);
		}

		DisconnectRequest discReq = new DisconnectRequest();

		discReq.setIpAddress(clientIP);
		discReq.setClientName(clientName.trim());
		discReq.setPort(clientPort);

		System.out.print("getDisconnectReqDetails:" + discReq.toString());
		return discReq;
	}

	private String stripNewLineChar(String strVal) {

		System.out.print("String for strip:" + strVal);
		int length = strVal.trim().length();

		strVal = strVal.trim().replaceAll("(\\r|\\n|\\r\\n)+", "");

		if (length == strVal.length()) {
			strVal = strVal.substring(0, strVal.length() - 2);
		}

		System.out.print("After strip:" + strVal);
		return strVal;
	}

	private synchronized String manageLeaveCRMessage(LeaveChatroomRequest leaveRequest) {

		System.out.print("manageLeaveCRMessage:Request" + leaveRequest.toString());

		// String Chatroom_Name = "";
		// String JOIN_ID = "";
		// String CLIENT_NAME = "";
		//
		// Matcher Join_Chatroom_Request_matcher =
		// Pattern.compile(Leave_Chatroom_Request).matcher(input);
		//
		// while (Join_Chatroom_Request_matcher.find()) {
		//
		// Chatroom_Name = Join_Chatroom_Request_matcher.group(1);
		// JOIN_ID = Join_Chatroom_Request_matcher.group(2);
		// CLIENT_NAME = Join_Chatroom_Request_matcher.group(3);
		// }

		String response = String.format(Leave_Chatroom_Response, leaveRequest.getChatRoomRef(), leaveRequest.getClientJoinID());
		System.out.print("manageLeaveCRMessage:Response" + response);
		return response;
	}

	private synchronized LeaveChatroomRequest getLeaveCRReqDetails(String input) {

		System.out.print("getLeaveCRReqDetails Request:" + input);

		String chatroomName = "";
		String clientJoinID = "";
		String clientName = "";

		Matcher Join_Chatroom_Request_matcher = Pattern.compile(Leave_Chatroom_Request).matcher(input);

		while (Join_Chatroom_Request_matcher.find()) {

			chatroomName = Join_Chatroom_Request_matcher.group(1);
			clientJoinID = Join_Chatroom_Request_matcher.group(2).trim();
			clientName = Join_Chatroom_Request_matcher.group(3);
		}

		if (stripNewLineChar) {
			chatroomName = stripNewLineChar(chatroomName);
			clientJoinID = stripNewLineChar(clientJoinID);
			clientName = stripNewLineChar(clientName);
		}

		LeaveChatroomRequest leaveRequest = new LeaveChatroomRequest();
		leaveRequest.setChatRoomRef(chatroomName.trim());
		leaveRequest.setClientJoinID(Integer.valueOf(clientJoinID));
		leaveRequest.setClientName(clientName.trim());
		System.out.print("getLeaveChatroomReqDetails Response:" + leaveRequest.toString());
		return leaveRequest;
	}
	
//Dealing with Helo Message
	/*Your service should also respond to the message:
	  "HELO text\n"
	
with the string
	  "HELO text\nIP:[ip address]\nPort:[port number]\nStudentID:[your student ID]\n"*/

	public synchronized String DecipherHeloMessage(String inputMessage) throws UnknownHostException {

		Matcher Join_Chatroom_Request_matcher = Pattern.compile(heloText).matcher(inputMessage);

		//This is the response to the HELO input
		String response = String.format(HELO_RESPONSE, inputMessage, serverIPAddress, server.getLocalPort(), "12316657");
		return response;

	}

	private synchronized String manageChatroomMessages(String input) {

		System.out.print("manageChatroomMessages:Request:" + input);

		String Chatroom_Name = "";
		String JOIN_ID = "";
		String CLIENT_NAME = "";
		String CLIENT_MESSAGE = "";

		Matcher Join_Chatroom_Request_matcher = Pattern.compile(Chatroom_Message).matcher(input);

		while (Join_Chatroom_Request_matcher.find()) {

			Chatroom_Name = Join_Chatroom_Request_matcher.group(1);
			JOIN_ID = Join_Chatroom_Request_matcher.group(2);
			CLIENT_NAME = Join_Chatroom_Request_matcher.group(3);
			CLIENT_MESSAGE = Join_Chatroom_Request_matcher.group(4);
		}

		stripNewLineChar(Chatroom_Name);

		String response = String.format(Chatroom_Message_Response, Chatroom_Name, CLIENT_NAME, CLIENT_MESSAGE);
		System.out.print("manageChatroomMessages:Response:" + response);
		return response;
	}

	private synchronized ChatRequest getChatroomMessage(String input) {

		System.out.print("getChatroomMessage:Request:" + input);

		String ChatroomReference = "";
		String clientJoinID = "";
		String clientName = "";
		String clientMessage = "";

		Matcher Join_Chatroom_Request_matcher = Pattern.compile(Chatroom_Message).matcher(input);

		while (Join_Chatroom_Request_matcher.find()) {

			ChatroomReference = Join_Chatroom_Request_matcher.group(1);
			clientJoinID = Join_Chatroom_Request_matcher.group(2);
			clientName = Join_Chatroom_Request_matcher.group(3);
			clientMessage = Join_Chatroom_Request_matcher.group(4);
		}

		// stripNewLineChar(chatroomName);

		// Chatroom_Message_Response= String.format(Chatroom_Message_Response, Chatroom_Name,CLIENT_NAME,
		// CLIENT_MESSAGE);

		if (stripNewLineChar) {

			ChatroomReference = stripNewLineChar(ChatroomReference);
		}

		ChatRequest chatReq = new ChatRequest();
		chatReq.setChatRoomRef(ChatroomReference.trim());
		chatReq.setClientName(clientName.trim());
		chatReq.setMessage(clientMessage.trim());
		chatReq.setclientJoinID(clientJoinID.trim());

		System.out.print("getChatroomMessage:Response:" + chatReq.toString());
		return chatReq;
	}

	public synchronized void remove(int ID) {
		int pos = findClient(ID);
		if (pos >= 0) {
			ChatServerSupport toTerminate = clientArray[pos];
			/*
			 * System.out.println("Removing client thread " + ID + " at " +
			 * pos); if (pos < clientCount - 1) for (int i = pos + 1; i <
			 * clientCount; i++) clientArray[i - 1] = clientArray[i];
			 * clientCount--;
			 */
			try {
				toTerminate.close();
			} catch (IOException ioe) {
				System.out.print("Error closing thread: " + ioe);
			}
			toTerminate.stop();
		}
	}

	private void addThread(Socket socket) {
		if (clientCount < clientArray.length) {
			System.out.print("Client accepted: " + socket + clientCount);
			clientArray[clientCount] = new ChatServerSupport(this, socket);

			try {
				clientArray[clientCount].open();
				clientArray[clientCount].start();
				clientCount++;
			} catch (IOException ioe) {
				System.out.print("Error opening thread: " + ioe);
			}
		} else
			System.out.print("Max lenght " + clientArray.length + " reached.");
	}

	public static void main(String args[]) {
		int portNumber = 4444;
//		if (args != null && args[0].trim().length() > 0) {
//
//			String arg0 = args[0];
//			portNumber = Integer.valueOf(arg0);
//		}
		ChatServer server = new ChatServer(portNumber);
	}
}
