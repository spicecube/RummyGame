package launcher;
import java.io.IOException;
import java.net.*;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

//Server Class that essentially displays the GUI for the server as well as calls the 
// CommunicationThread Class which sets up connections to all clients
public class Server extends JFrame implements ActionListener{
private static final long serialVersionUID = 1L;
	private boolean running;	//Boolean to determine if the server is running
	JButton start = new JButton("Start Server");	//Jbutton to start server
	// Network Items
	boolean serverContinue;							//
	private String machineAddress;
	@SuppressWarnings("unused")
	private int port;
	ServerSocket serverSocket;
	JTextField Port = new JTextField("Port: 00000");	//JTextFields that displays IP and Port
	private JTextField IP = new JTextField("IP: ");

	// set up GUI
	public Server() {	//Server Constructors creates the GUI for the server
		super("Server");
		Container container = getContentPane();	//Container to contain server gui
	    container.setLayout( new FlowLayout() );

	    container.add(Port);
	    container.add(IP);

		// get content pane and set its layout
	    running = false;
		start = new JButton("Start Server");
		start.addActionListener(this);
		container.add(start);
		try {
			InetAddress addr = InetAddress.getLocalHost();	//Sets TextField equal to host IP
			setIP(addr.getHostAddress());
		} catch (UnknownHostException e) {
			machineAddress = "192.168.0.8";
		}
		IP.setText("IP: "+machineAddress);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();	//gets default location
		int x = (int) ((dimension.getWidth() - 350) / 2);
		int y = (int) ((dimension.getHeight() - 75) / 2);
		setLocation(x,y);
		setSize(350, 75 );
	    setVisible( true );
	} // end Server constructor

	public void setIP(String address){	//Getter methods to set ip and port 
		machineAddress = address;
	}
	public void setPort(int port){
		this.port = port;
	}
	//Action Listener that allows the user to start the server or disconnect 
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == start && start.getText().equals("Disconnect")){
			try {
				serverSocket.close();
				start.setText("Start Server");
				Port.setText("Port: 00000");
				serverContinue = false;
			} catch (IOException e1) {
			}
		}
		else if (running == false && e.getSource() == start) {			//If they turn on the server they can start the connectionthread
			new ConnectionThread(this);
			start.setText("Disconnect");
		}
	}
} // end class Server
