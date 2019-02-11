import java.net.*;
import java.io.*;

/**
 * @author pelage
 *
 */
public class ReceiveUDP2 {

  /**
 * @param args
 * @throws IOException
 */
public static void main(String[] args) throws IOException {

    DatagramPacket p;
    MulticastSocket s = new MulticastSocket(7654);

    s.joinGroup(InetAddress.getByName("224.0.0.1"));

    //en écoute
    while (true) {
    	p = new DatagramPacket(new byte[512],512);
    	s.receive(p);
    	System.out.println("paquet reçu de : "+ p.getAddress()+
    		    " port : "+            p.getPort()+
    		    " taille : " +          p.getLength());
    	byte array[] = p.getData();
    	String res = new String(array);
    	System.out.println(res);
    }

  }
}
