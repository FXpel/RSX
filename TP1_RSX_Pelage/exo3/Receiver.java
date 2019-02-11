import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Receiver extends Thread {

	InetAddress ip;
	   int port;
	   int i;
	   char c;
	   String nom;
	   MulticastSocket socketReception;

	   Receiver(InetAddress groupeIP, int port, String nom)  throws Exception { 
		   this.ip = groupeIP;
		   this.port = port;
		   this.nom = nom;
		   socketReception = new MulticastSocket(port);
		   socketReception.joinGroup(groupeIP);
		   start();
	  }

	  public void run() {
	    DatagramPacket message;
	    byte[] contenuMessage;
	    String texte;
	    while(true) {
			  contenuMessage = new byte[1024];
			  message = new DatagramPacket(contenuMessage, contenuMessage.length);
			  try {
		              socketReception.receive(message);
		              contenuMessage = message.getData();
		              System.out.print("user "+message.getSocketAddress().toString().substring(11, 14)+": ");
		              texte = new String(contenuMessage);
		              System.out.println(texte);
			  }
			  catch(Exception exc) {
		    		System.out.println("Erreur run() Receiver");
			  }
	    }
	  }

}
