import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.rmi.UnknownHostException;
import java.net.InetAddress;

/**
 * @author pelage
 *
 */
public class SendUDP2 {

  /**
 * @param args
 * @throws IOException
 */
public static void main(String[] args) throws IOException {

    DatagramPacket p;
    MulticastSocket s;
    int port = 7654;
    byte array[];
    String msg = "";
    int i=0;
    InetAddress dst;
    while(i<args.length){
      msg += args[i]+" ";
      i++;
    }
    dst = InetAddress.getByName("224.0.0.1");
    
    array = msg.getBytes();

    p = new DatagramPacket(array, array.length, dst, port);
    s = new MulticastSocket();

    s.send(p);

    s.close();

  }
}
