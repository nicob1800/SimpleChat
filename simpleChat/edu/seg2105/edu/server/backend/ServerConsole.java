package edu.seg2105.edu.server.backend;

/**
 * 
 */

import edu.seg2105.client.common.ChatIF;
import java.io.*;

/**
 * 
 */
public class ServerConsole implements ChatIF {

	
    EchoServer server;
	
    public ServerConsole(EchoServer server) {
    		this.server = server;
    		try {
    			BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
    			String message;
    			
    			while(true) {
    				message = fromConsole.readLine();
    				server.handleMessageFromServerUI(message);
    			}
    		}catch (Exception e) {
				System.out.println("Error occurred when reading from console");
			}
    }
    
    
	@Override
	public void display(String message) {
		// TODO Auto-generated method stub
		System.out.println(message);	
	}

	
}
