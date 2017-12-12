package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatServerSupport extends Thread {
	private ChatServer server = null;
	private Socket socket = null;
	private int ID = -1;
	private BufferedReader streamIn = null;
	private PrintWriter streamOut = null;

	// private DataInputStream streamIn = null;
	// private DataOutputStream streamOut = null;

	private Scanner scanner = null;
	AtomicInteger atomicInteger = new AtomicInteger();

	public ChatServerSupport(ChatServer _server, Socket _socket) {
		super();
		server = _server;
		socket = _socket;
		ID = socket.getPort();
		//ID = atomicInteger.getAndIncrement();
	}

	public void send(String msg) {
		try {
			streamOut.print(msg);
			// streamOut.writeUTF(msg);
			streamOut.flush();
		} catch (Exception ioe) {
			System.out.print(ID + " ERROR sending: " + ioe.getMessage());
			server.remove(ID);
			stop();
		}
	}

	public int getID() {
		return ID;
	}

	public void run() {
		System.out.print("Server Thread " + ID + " running.");

		StringBuffer sb = new StringBuffer();
		String line = null;
		while (true) {
			try {

				while ((line = streamIn.readLine()) != null) {

					String msg = line;
					sb.append(msg);
//					if (!msg.contains("HELO")) {
//
//						sb.append("\n");
//					}

					// String msg = line;
					// System.out.print("got enter:" + msg);

					// server.handle(ID, msg);
//Puts string into one line so it can be read by server
					String sbStr = sb.toString();
					System.out.print("Completed:" + sbStr);
					if ((sbStr.contains("HELO")) || (sbStr.contains("KILL_SERVICE"))
							|| (sbStr.contains("JOIN_CHATROOM") && sbStr.contains("CLIENT_NAME"))
							|| (sbStr.contains("LEAVE_CHATROOM") && sbStr.contains("CLIENT_NAME"))
							|| (sbStr.contains("DISCONNECT") && sbStr.contains("CLIENT_NAME"))
							|| (sbStr.contains("CHAT") && sbStr.contains("MESSAGE"))
							) {
						System.out.print("Brken:" + sbStr);
						server.handle(ID, sbStr);
						sb.setLength(0);
						break;
					}

				}

			} catch (Exception oe) {
				System.out.print(ID + " ERROR reading: " + oe.getMessage());
				server.remove(ID);
				stop();
			}
		}
	}

	public void open() throws IOException {
		streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		streamOut = new PrintWriter(socket.getOutputStream(), true);

		// streamIn = new DataInputStream(new
		// BufferedInputStream(socket.getInputStream()));
		// streamOut = new DataOutputStream(new
		// BufferedOutputStream(socket.getOutputStream()));

		// scanner = new Scanner(socket.getInputStream());
	}

	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (streamIn != null)
			streamIn.close();
		if (streamOut != null)
			streamOut.close();
	}
}