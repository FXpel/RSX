import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Sender extends Thread {

	InetAddress ip;
	int port;
	MulticastSocket socketEmission;
	String nom;
	  
    Sender(InetAddress groupeIP, int port, String nom) throws Exception {
		this.ip = groupeIP;
		this.port = port;
		this.nom = nom;
		socketEmission = new MulticastSocket();
		start();
  }
	    
	  public void run() {
	    BufferedReader entreeClavier;
	    try {
			emettre(nom+ " est en ligne.",true);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	    try {
	       entreeClavier = new BufferedReader(new InputStreamReader(System.in));
	       while(true) {
				  try {
					  String texte = entreeClavier.readLine();
					  emettre(texte, false);
				  } catch (Exception e) {
					  System.out.println("ICI");
				  }
				  
	    	   	  
	       }
	    }
	    catch (Exception exc) {
	       System.out.println("Erreur run() Sender");
	    }
	  } 

	  void emettre(String texte, boolean co) throws Exception {
			byte[] contenuMessage;
			DatagramPacket message;
			if (!co) {
				texte = nom + " : " + texte ;
			}
			contenuMessage = texte.getBytes();
			message = new DatagramPacket(contenuMessage, contenuMessage.length, ip, port);
			socketEmission.send(message);
	  }

}
