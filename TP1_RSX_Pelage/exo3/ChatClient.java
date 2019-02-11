import java.net.*;
import java.io.*;

/**
 * @author roussela
 * 
 */
public class ChatClient {
	
	public static void main(String[] args) throws Exception {
		String nom = args[0];
		InetAddress ip = InetAddress.getByName("224.0.0.1");
		int port = 7654;
		
		new Sender(ip, port, nom);
		new Receiver(ip, port, nom);
	}
}
