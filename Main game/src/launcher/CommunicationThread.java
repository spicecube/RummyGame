package launcher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
//CommunicationThread is a class that essentially handles the data flow from client to server and
//back to client
public class CommunicationThread extends Thread {
	private Socket clientSocket;	//Stores current socket being read
	private ConnectionThread server;	//Uses the instance of connectionthread in order to message users

	//Main constructor that starts reading in client input
	public CommunicationThread(ConnectionThread server, Socket clientSoc) {
		clientSocket = clientSoc;
		this.server = server;
		start();
	}
	//Getter function
	public Socket getSocket(){
		return clientSocket;
	}
	//Parsing functions. This one parses for the Sending User name
	public String getUser(String temp){
		String delims = ":";
		String[] tokens = temp.split(delims);
		String user = tokens[3];
		return user;
	}
	//Parsing functions. This one parses for the Message being sent
	public String getMsg(String temp){
		String delims = ":";
		String[] tokens = temp.split(delims);
		String msg = tokens[4];
		return msg;
	}
	//Parsing Functions. This one parses for the Recipient user
	public String getTo(String temp){
		String delims = ":";
		String[] tokens = temp.split(delims);
		String To = tokens[1];
		return To;
	}
	//Run that runs simultaneously
	public void run() {
		//Try and catch which reads in user input
		try {
			//Opens an input and output stream
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
					true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			String inputLine;
			//While loop that reads in input
			while ((inputLine = in.readLine()) != null) {
				//If the input starts with a To it sends a private message
				if(inputLine.startsWith("To")){
					String from = getUser(inputLine);
					String message = getMsg(inputLine);
					String to = getTo(inputLine);
					ChatMessage temp = new ChatMessage(" "+message, from, to, getSocket());
					server.messages.add(temp);
					server.sendToSpecifiedUser();
				}
				//If the input starts with Bya it disconnects the client
				else if (inputLine.equals("Bye.")){
					server.removeConnection(clientSocket);
				}
				//If anything else the message gets sent to everyone online
				else{
					out.println(inputLine);
					server.sendToAll(inputLine, getSocket());
				}
			}
			//Closes streams and closes socket
			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			//System.exit(1);
		}
	}
}