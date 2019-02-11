import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author pelage
 *
 */
public class SendUDP {

  /**
 * @param args
 * @throws IOException
 */
public static void main(String[] args) throws IOException {

    DatagramPacket p;
    DatagramSocket s;
    int port;
    byte array[];
    String msg = "";
    int i=0;
    while(i<args.length){
      msg += args[i]+" ";
      i++;
    }
    
    port = Integer.parseInt(args[1]);
    InetAddress dst = InetAddress.getByName(args[0]);
    array = msg.getBytes();


    p = new DatagramPacket(array, array.length, dst, port);
    s = new DatagramSocket();
    s.send(p);
    s.close();

  }
}
