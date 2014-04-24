package launcher;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

//Main class that brings everything together. Essentially the client class that sets up the gui
//and connects the client or sets up a server
public class ChatGUI extends JFrame implements ActionListener, Runnable, MouseListener{
	private static final long serialVersionUID = 1L; //makes eclipse happy =)
	Scanner reader = new Scanner(System.in);								//Several different variable											
	private static JPanel p1, p2, p3;										//Declerations
	private JLabel userLabel = new JLabel("               Users Online:");
	private static JMenuBar menu = new JMenuBar();							//Menubar
	private JMenu m1 = new JMenu("Menu");									//Menu
	private JMenuItem HostServer, Connect;									//JMenuitems..
	private JMenu help = new JMenu("Help");
	private JMenuItem ServerSetup, ClientConnect, Messaging;
	private JButton [] options = new JButton[3];							//Jbuttons for Msg, Msg All, and Dc
    private JTextField textField;											//Textfields and TextArea for chat
    private JTextArea textArea;
    private JTextArea usersArea;
    boolean connected;		
    private String sendTo = null;													//Strings to keep track of
    private String Username;												//Users being sent msg's and 
    private Socket echoSocket;												//Individual client names
    private PrintWriter out;												//Streams to comm. with server
    private BufferedReader in;

	//Main constructor which sets up and implements the GUI for the program
	public ChatGUI(){
		super("Chat Client");
		p1 = new JPanel(new GridLayout(1,3));	//3 panels for buttons users online and chat
		p2 = new JPanel(new GridLayout(2,1));
		p3 = new JPanel(new GridBagLayout());
		
		Dimension d = new Dimension(1,1);				//Setting online userpanel
		userLabel.setPreferredSize(d);				
		p2.add(userLabel);
		usersArea = new JTextArea(10, 15);
		usersArea.setEditable(false);
		usersArea.addMouseListener(this);
        JScrollPane scroll = new JScrollPane(usersArea);

        p2.add(scroll);								//Adding it to the 2nd panel 
		
		options[0] = new JButton("Message");			//Implementing buttons ass well as adding them
		options[1] = new JButton("Message All");		// to the 1st panel and adding actionlistners to 
		options[2] = new JButton("Disconnect");			//them
		
		for(int j=0; j<3; j++){						//Adding...
			options[j].addActionListener(this);
			p1.add(options[j]);
		}
		
		HostServer = new JMenuItem("HostServer");		//Sets up multiple JMenu items
		Connect = new JMenuItem("Connect");				//Adds acction listeners to them
		HostServer.addActionListener(this);
		Connect.addActionListener(this);
		m1.add(HostServer);
		m1.add(Connect);
		menu.add(m1);
		
		ServerSetup = new JMenuItem("ServerSetup");				//More JMenuItems being set up..
		ClientConnect = new JMenuItem("ClientConnect");
		Messaging = new JMenuItem("Messaging");
		ServerSetup.addActionListener(this);
		ClientConnect.addActionListener(this);
		Messaging.addActionListener(this);
		help.add(ServerSetup);
		help.add(ClientConnect);
		help.add(Messaging);
		menu.add(help);
		
		textField = new JTextField(20);							//Sets up textField and textArea
        textField.addActionListener(this);						//One are for typing messages
		textArea = new JTextArea(5, 20);						//And the other being the container
        textArea.setEditable(false);							//for the chat
        JScrollPane scrollPane = new JScrollPane(textArea);
         
        //adding components to this panel 	
        GridBagConstraints gridConstraint = new GridBagConstraints();
        gridConstraint.gridwidth = GridBagConstraints.REMAINDER;

        gridConstraint.fill = GridBagConstraints.HORIZONTAL;
        p3.add(textField, gridConstraint);

        gridConstraint.fill = GridBagConstraints.BOTH;
        gridConstraint.weightx = 1.0;
        gridConstraint.weighty = 1.0;
        p3.add(scrollPane, gridConstraint);					//Adds the chat to panel 3
	}
	
	//Getter and setter functions
	public String getUsername(){
		return this.Username;
	}
	
	public void setUsername(String username){
		this.Username = username;
	}
	
	@Override//General actionperformed method for the event listener
	public void actionPerformed(ActionEvent e) {
		//Sends the message to everyone
		if(connected && (e.getSource() == textField || e.getSource() == options[1]) ){
			sendMessage();
		}
		
		//Sends a private message
		else if( connected && (e.getSource() == textField || e.getSource() == options[0]) ) {
			sendPrivateMessage(sendTo);
		}
		
		//Disconnects the client from the server
		else if(connected && e.getSource() == options[2]) {
			textArea.append("\nDisconnected\n");
			try {
				out.println("Bye.");
				out.close();
				in.close();
				usersArea.setText("");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		//Connects the client to a server
		if(e.getSource() == Connect){
			String address = JOptionPane	//Displays the following message to the user and prompts for the
					.showInputDialog(		// server that they want to connect to
							this,
							"Enter Server and Port to connect to...Ex: IP:Port");
			ManageConnection(address);
		}
		
		//Allows the user to host a new server
		else if(e.getSource() == HostServer){
			new Server();
		}
		
		//Several options that process the menu options
		if(e.getSource() == ServerSetup) {
			JOptionPane.showMessageDialog(this,
					"The server is vital for messaging other clients and needs to remain open for the entirety\n"
					+ "of the chat session...\n" 
					+ "To create a server, have any user act as the server host. Go to Menu->HostServer and\n"
					+ "select the Start Server option. You may specify a desired port number or default.\n",
  				    "Server Setup", JOptionPane.PLAIN_MESSAGE);
		}
		else if(e.getSource() == ClientConnect) {
			JOptionPane.showMessageDialog(this,
					"A server must be setup prior to any client connection, as there would be nothing to connect to...\n"
					+ "Have each user connect their clients to the server by going to Menu->Connect and enter the\n"
					+ "host's IP Adress and Port Number. These two numbers will need to be acquired from the host.\n", 
  				    "Connecting Clients", JOptionPane.PLAIN_MESSAGE);
		}
		else if(e.getSource() == Messaging) {
			JOptionPane.showMessageDialog(this,"Message All: To message all Users you can type in the box and press message all to do so\n"
					+"\nPrivate Message: To message one individual User you may click their name on the side\n"
  				    + "panel where the online users are displayed until it gets highlighted gray type a message\n"
  				    + "and press message!\n NOTE IT MAY REQUIRE MORE THAN ONE CLICK TO HIGHLIGHT THE NAME!!!!\n"
  				    + "\nDisconnect: To disconnect all you need to do is press the disconnect button and you will be \n"
  				    + "disconnected from the server.", "Messagin Options", JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	//MouseClicket handles the clicking of certain users from the online users panel
	public void mouseClicked(MouseEvent e) {
		//Assigns sendTo to the user that got clicked/highlighted
		if(usersArea.getSelectedText() !=null){
			sendTo = usersArea.getSelectedText();
		}
	}
	//Empty method needed for mouselistener
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	
	//SendMessage essentially sends the server the message that is to be sent to all
	//the clients
	public void sendMessage() {
		String msg = null;
		if(textField.getText().equals("")){
			msg = " ";
		}
		else{
			msg = textField.getText();
		}
		String message = getUsername() + ": " + msg;
        out.println(message); //Sends the message to the server
        textField.setText(""); //clear textfield
	}
	
	//SendPrivateMessage essentially sends the server the message that is to be sent to
	//One specific user
	public void sendPrivateMessage(String To){
		String msg = null;
		if(textField.getText().equals("")){
			msg = " ";
		}
		else{
			msg = textField.getText();
		}
		if(To!=null){
			String message = "To:"+To+":From:"+getUsername()+":"+msg;
			out.println(message);	//Sends the private message to the user
			textArea.append("PRIV MSG TO - "+To+"\n"+Username+": "+textField.getText()+"\n");
			textField.setText("");
		}
	}
	
	//Essentially returns false if the user is already in the online list
	public boolean userOnline(String userArea, String user){
		String delims = "\n";
		String[] tokens = userArea.split(delims);	//Splits the textArea up and compares it
		for(int i = 0;i<tokens.length;i++){			//to the user trying to be added to the list
			if(tokens[i].equals(user)){
				return false;
			}
		}
		return true;
	}
	// Background thread runs this: show messages from other window
	public void run() {
		try {
			// Receive messages one-by-one, forever
			while (true) {
				// Get the next message
				String message = in.readLine();
				//If message is null then that means the user is disconnected from the server 
				if(message == null){
					textArea.append("\nDISCONNECTED FROM SERVER\n");
					usersArea = new JTextArea("");
				}
				
				//Updates online users
				else if((message.length() >=5) && (message.substring(0, 4)).equals("User")){
					String delims = ":";
					String[] tokens = message.split(delims);
					if(userOnline(usersArea.getText(), tokens[1])){
						usersArea.append(tokens[1]+"\n");
					}
				}
				
				//Updates offline users
				else if((message.length() >=8) && message.substring(0,7).equals("Offline")){
					String delims = ":";
					String[] tokens = message.split(delims);
					int n = usersArea.getText().indexOf(tokens[1]);
					usersArea.replaceRange("", n, n+tokens[1].length());
				}
				
				//Updates general all chat
				else{
					// Print it to our text window
					textArea.append(message + "\n");
				}
			}
		} catch (IOException ie) {
			System.out.println(ie);
		}
	}

	//Manage connection method essentially handles the connection from the server to the client
	public void ManageConnection(String address){
		if (address != null) {	//If the user entered an address that wasnt null then 
			String machineName = null;
			int portNum = -1;
			//Essentially connects to the server
			try {
				String delims = ":";
				String[] tokens = address.split(delims);
				if(!(address.equals(""))){
					String port = tokens[1];
					machineName = tokens[0];
					portNum = Integer.parseInt(port);
				}
				
				if(machineName !=null && portNum!= -1){
					echoSocket = new Socket(machineName, portNum);	//Creates socket and input and output streams
					out = new PrintWriter(echoSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(
							echoSocket.getInputStream()));
					String username = JOptionPane	//Displays the following message to the user and prompts for the
							.showInputDialog(		// server that they want to connect to
									this,
									"Enter a username that you want to be known as: ");
					setUsername(username);
					out.println(Username);
					connected = true;
					String connection = in.readLine();
					textArea.append(connection+"\n");
					new Thread(this).start();
				}
				else{
					JOptionPane.showMessageDialog(this,
							"No IP or Port Entered!\n","Error", JOptionPane.PLAIN_MESSAGE);
				}
			} catch (NumberFormatException e) {
			} catch (UnknownHostException e) {
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this,
						"Couldn't get I/O for the connection to: "
								+ machineName,"Error", JOptionPane.PLAIN_MESSAGE);
			}
		}

	}
	
	//Main method to bring everything back together
	public static void main(String[] args){
		ChatGUI test1 = new ChatGUI();
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();	//gets default location
		int x = (int) ((dimension.getWidth() - 300) / 2);
		int y = (int) ((dimension.getHeight() - 600) / 2);

		test1.setLocation(x, y);										//Sets location for the board
		test1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);	//General exit operation
		test1.setJMenuBar(menu);//Adds the menu to the GUI
		test1.add(p1, BorderLayout.SOUTH);								//Adds the panels to the JFrame
		test1.add(p2, BorderLayout.EAST);
		test1.add(p3, BorderLayout.CENTER);
		test1.setSize(600, 300);										//Sets the size for the JFrame
		test1.setVisible(true);											//Sets the JFrame to be visible and
		test1.setResizable(true);	
	}

}
