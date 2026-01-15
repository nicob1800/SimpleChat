package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.*;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	String message = msg.toString();
	if(message.startsWith("#login")) {
		String loginID = (String)client.getInfo("loginID");
		System.out.println("Message received: " + message + " from " + client);
		if(loginID != null) {
			try {
				client.sendToClient("Already logged in. Connection terminated.");
				client.close();
			}catch (IOException e) {}
			return;
		}
		
		String[] parts = message.split(" ", 2);
		if(parts.length < 2) {
			try {
				client.sendToClient("Login ID is required. Connection terminated.");
				client.close();
			}catch(IOException e) {}
			return;
		}
		
		loginID = parts[1];
		client.setInfo("loginID", loginID);
		
		System.out.println(loginID + " has logged on");
		this.sendToAllClients("SERVER MSG> " + loginID + " has logged on.");
	}else {
		String loginID = (String)client.getInfo("loginID");
		if(loginID == null) {
			try {
				client.sendToClient("Must login with #login <id> first. Connection terminated.");
				client.close();
			}catch(IOException e) {}
			return;
		}
		String prefixedMessage = loginID + "> " + message;
		System.out.println("Message received: " + message + " from " + loginID);
		
		this.sendToAllClients(prefixedMessage);
	}
	
	
	
    //System.out.println("Message received: " + msg + " from " + client);
    //this.sendToAllClients(msg);
  }
  
  public void handleMessageFromServerUI(String message) {
	  
	  String[] commandParts = message.split(" ", 2);
	    String commandName = commandParts[0];
	    String commandArg = commandParts.length > 1 ? commandParts[1] : "";
	    
	    // Check for commands (Exercise 2c)
	    if (commandName.startsWith("#")) {
	        
	        try {
	            if (commandName.equals("#quit")) {
	                this.sendToAllClients("SERVER MSG> Server is shutting down.");
	                this.close();
	                System.out.println("Server quitting");
	                System.exit(0);
	                
	            } else if (commandName.equals("#stop")) {
	                if (isListening()) {
	                    stopListening();
	                    
	                    //System.out.println("Server stopped listening for new connections.");
	                } else {
	                    System.out.println("Server is already stopped (not listening).");
	                }
	                
	            } else if (commandName.equals("#close")) {
	                this.close();
	                //System.out.println("Server closed: stopped listening and disconnected all clients.");
	                
	            } else if (commandName.equals("#setport")) {
	                if (!isListening() && getNumberOfClients() == 0) {
	                    int newPort = Integer.parseInt(commandArg);
	                    setPort(newPort);
	                    System.out.println("Port set to: " + getPort());
	                } else {
	                    System.out.println("Error: Cannot change port while listening or with connected clients. Use #close first.");
	                }
	                
	            } else if (commandName.equals("#start")) {
	                if (!isListening()) {
	                    listen();
	                } else {
	                    System.out.println("Error: Server is already listening. Use #stop first.");
	                }
	                
	            } else if (commandName.equals("#getport")) {
	                System.out.println("Current port: " + getPort());
	                
	            } else {
	                System.out.println("Error: Unrecognized server command: " + commandName);
	            }
	        } catch (IOException e) {
	            System.out.println("An I/O error occurred during command execution: " + e.getMessage());
	        } catch (NumberFormatException e) {
	            System.out.println("Error: Invalid port number provided for #setport.");
	        }

	    } else {
	        // Handle regular messages (Exercise 2b)
	        // Note: We use "SERVER MSG> " here, assuming MESSAGE_PREFIX is defined that way in ServerConsole
	        String prefixedMessage = "SERVER MSG> " + message;
	        
	        // Echo to server console (self-echo)
	        System.out.println(prefixedMessage); 
	        
	        // Echo to all clients
	        this.sendToAllClients(prefixedMessage);
	    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for clients on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("A new client has connected to the server.");
  }
  
  @Override
  protected void clientDisconnected(ConnectionToClient client) {
	  String loginID = (String)client.getInfo("loginID");
	  
	  if(loginID != null) {
		  System.out.println(loginID + " has disconnected.");
		  this.sendToAllClients("SERVER MSG> " + loginID + " has disconnected.");
	  } else {
		  System.out.println("An unknown client has disconnected: " + client);
	  }
	  
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on
    
    try
    {
    		
      port = Integer.parseInt(args[0]); //Get port from command line
      
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);

    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    
   
    ServerConsole sc = new ServerConsole(sv);

    
  }
}
//End of EchoServer class
