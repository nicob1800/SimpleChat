// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  private String loginID;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
	
    super(host, port);
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }
  
  @Override
  protected void connectionEstablished() {
	  try {
		  sendToServer("#login " + loginID);
	  }catch(IOException e){
		  clientUI.display("No id given, disconnect");
	  }
  }
  

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
	   	if(message.startsWith("#")){
	    		handleCommand(message);
	    	}else {
	    		sendToServer(message);
	    	}
    		
      
      
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  private void handleCommand(String command) {
	  String[] commandParts = command.split(" ", 2);
	  String commandName = commandParts[0];
	  String commandArg = commandParts.length > 1 ? commandParts[1] : "";
	  
	  try {
	  if(command.equals("#quit")) {
		  //clientUI.display("Quitting");
		  quit();
	  }else if(command.equals("#logoff")) {
		  if(isConnected()) {
			  closeConnection();
			  clientUI.display("Connection closed");
		  }else {
			  clientUI.display("Already logged off");
		  }
	  }else if(commandName.equals("#sethost")) {
		  if(!isConnected()) {
			  setHost(commandArg);
			  clientUI.display("Host set to: " +commandArg);
		  }else {
			  clientUI.display("Already connected to a host. Logoff first");
		  }
	  }else if(commandName.equals("#setport")) {
		  if(!isConnected()){
			  setPort(Integer.parseInt(commandArg));
			  clientUI.display("Port set to: " + commandArg);
		  }else {
			  clientUI.display("Already connected to a port. Logoff first");
		  }
	  }else if(command.equals("#login")) {
		  if(!isConnected()) {
			  openConnection();
			  clientUI.display("Attempting to connect to: " + getHost() + ":" + getPort());
		  }else {
			  clientUI.display("Already connected");
		  }
	  }else if(commandName.equals("#gethost")) {
		  clientUI.display("Current host: " + getHost());
	  }else if(commandName.equals("#getport")) {
		  clientUI.display("Current port: " + getPort());
	  }else {
		  clientUI.display("Invalid command entered: " + command);
	  }
  }
	  catch (IOException e) {
		  clientUI.display("A connection error has occurred: " + e.getMessage());
		  quit();
	  }catch (NumberFormatException ne) {
		  clientUI.display("Error: invalid port number");
	  }
	  
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  @Override
  protected void connectionException(Exception exception) {
	  clientUI.display("The server has shut down");
	  quit();
  }
  
  @Override
  protected void connectionClosed() {
	  //clientUI.display("Connection closed");
  }
  
  
}
//End of ChatClient class
