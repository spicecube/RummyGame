package launcher;
import java.net.Socket;

//Java Encapsulation Class that Stores Username and Socket information from each client
public class Clients {
	private Socket sockets;		//Data being store
	private String userName;
	//Clients constructor stores socket and username
	public Clients(Socket sockets, String userName){
		this.sockets = sockets;
		this.userName = userName;
	}
	//Getter methods
	public String getUN(){
		return this.userName;
	}
	public Socket getSocket(){
		return this.sockets;
	}
}
