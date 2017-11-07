package server;

/*To terminate the client/server connection, a client will send the following message to the server, which should respond by terminating the connection.
DISCONNECT: [IP address of client if UDP | 0 if TCP]
PORT: [port number of client it UDP | 0 id TCP]
CLIENT_NAME: [string handle to identify client user]
*/


public class DisconnectRequest {

	String ipAddress = "";
	String port = "";
	String clientName = "";

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
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
		return "DisconnectRequest [ipAddress=" + ipAddress + ", port=" + port + ", clientName=" + clientName + "]";
	}

}
