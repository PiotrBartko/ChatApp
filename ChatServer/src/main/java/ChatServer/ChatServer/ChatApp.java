package ChatServer.ChatServer;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class ChatApp 
{
    public static void main( String[] args )throws IOException
    {
    	if (args.length ==0) 
    	{
    		return;
    		
    	}
    	int port = Integer.parseInt(args[0]);
    	ChatServer chat = new ChatServer(port);
    	System.out.println("Test");
    	chat.start();
    	
    }
    
}
