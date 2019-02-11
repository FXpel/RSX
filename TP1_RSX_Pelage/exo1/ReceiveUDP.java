import java.net.*;
import java.io.*;

/**
 * @author pelage
 *
 */
public class ReceiveUDP {

  /**
 * @param args
 * @throws IOException 
 */
public static void main(String[] args) throws IOException {

    DatagramSocket s;
    DatagramPacket p;

    s = new DatagramSocket(Integer.parseInt(args[0]));
    p = new DatagramPacket(new byte[512],512);
    
    //en écoute
    s.receive(p);
    
    System.out.println("paquet reçu de : "+ p.getAddress()+
    " port : "+            p.getPort()+
    " taille : " +          p.getLength());
    
    byte array[] = p.getData();
    
    String res = new String(array);
    
    System.out.println(res);
    
    s.close();

  }
}
