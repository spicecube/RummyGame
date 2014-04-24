package launcher;
import java.io.Serializable;
import java.net.Socket;

//Java Encapsulation Class that stores chat messages being set to and from 
public class ChatMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private String m;
	private String from;			//Data being encapsulated
	private String to;
	private Socket userSocket;
	//Main constructor that stores the encapsulated data
	public ChatMessage(String m1, String from, String to, Socket userSocket){
		this.m = from+":"+m1;
		this.from = from;
		this.to = to;
		this.userSocket = userSocket;
	}
	//Several Getter functions.
	public String getMessage(){
		return this.m;
	}
	public String getFrom(){
		return this.from;
	}
	public String getTo(){
		return this.to;
	}
	public Socket getSocket(){
		return this.userSocket;
	}
}
