package launcher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//ConnectionThread is essentially a class that deals with all incoming connections that are coming to the server
//and accepts them.
public class ConnectionThread extends Thread {
	//Has access to server gui
	Server gui;
	//Two array lists to keep track of messages being sent and clients connected
	ArrayList<Clients> clients = new ArrayList<Clients>();
	ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
	private int port;

	//Main constructor that starts this connection thread
	public ConnectionThread(Server es3) {
		gui = es3;
		start();
	}

	//Setters and getters
	public void setPort(int port){
		this.port = port;
	}
	public int getPort(){
		return this.port;
	}
	
	//Method that sends the message to a specific user
	public void sendToSpecifiedUser()throws IOException{
		synchronized(clients){//Synchronized in case clients disconnect
			int totalMsg = messages.size();
			for(int i=0;i<clients.size();i++){	//For loop That goes through all the clients until it 
				String temp = clients.get(i).getUN();	//finds the specific client to message
				ChatMessage msg1 = messages.get(totalMsg-1);
				String To = msg1.getTo();
				if(temp.equals(To)){
					Socket socket2Message = clients.get(i).getSocket();
					PrintWriter out = new PrintWriter(socket2Message.getOutputStream(),
							true);
					out.println("PRIV MSG FROM - "+msg1.getFrom()+"\n"+msg1.getMessage());
				}
			}
		}
	}
	
	// Send a message to all clients (utility routine)
	public void sendToAll(String message, Socket originator) throws IOException {
		// We synchronize on this because another thread might be
		// calling removeConnection() and this would screw us up
		// as we tried to walk through the list
		synchronized (clients) {
			// For each client ...
			for (int i = 0;i<clients.size();i++) {
				Socket temp = clients.get(i).getSocket();
				if (temp != originator) {
					// ... get the output stream ...
					PrintWriter out = new PrintWriter(temp.getOutputStream(),
							true);
					// ... and send the message
					out.println(message);
				}
			}
		}
	}
	//Removes disconnected users
	void removeConnection(Socket s) throws IOException {
		// Synchronize so we don't mess up sendToAll() while it walks
		// down the list of all output streams
		synchronized (clients) {
			String offline = null;
			// Remove it from our list
			for(int i =0;i<clients.size();i++){
				Socket temp = clients.get(i).getSocket();
				if(temp == s){
					offline = clients.get(i).getUN();
					clients.remove(i);
				}
			}
			//Update the Online User List for all other Clients
			for(int i =0;i<clients.size();i++){
				PrintWriter out = new PrintWriter(clients.get(i).getSocket().getOutputStream(),
						true);
				out.println("Offline:"+offline);
			}
			// Make sure it's closed
			try {
				s.close();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
	}
	//Run processes new connections being made between the client and server
	public void run() {
		gui.serverContinue = true;

		try {
			gui.serverSocket = new ServerSocket(0);
			try {
				while (gui.serverContinue) {
					setPort(gui.serverSocket.getLocalPort());//Accepts the socket and reads in the username for
					gui.Port.setText("Port: "+port);		//the socket
					
					Socket clientSocket = gui.serverSocket.accept();
					BufferedReader in1 = null;
					String userName = null;
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
							true);					//Opens output stream to each client;
					in1 = new BufferedReader(new InputStreamReader(	//Opens stream to update clients with info
							clientSocket.getInputStream()));
					userName = in1.readLine();
					
					out.println("Connected to Server!");
					
					Clients cl1 = new Clients(clientSocket, userName);	//Stores username with the socket it is 
					clients.add(cl1);									//Associated with 
					for(int i =0;i<clients.size();i++){
						out = new PrintWriter(clients.get(i).getSocket().getOutputStream(),
								true);					//Opens output stream to each client
						for(int j =0;j<clients.size();j++){
							String tempClient = clients.get(j).getUN();	//Populates Online user list
							out.println("User:"+tempClient);
						}
					}
					
					new CommunicationThread(this, clientSocket);
				}
			} catch (IOException e) {
			}
		} catch (IOException e) {
		} 
	}
	
	
}