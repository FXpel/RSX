import java.util.*;
import java.util.Arrays;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;
 import java.io.IOException;



public class Dns {

  public static void main(String[] args){

    Dns dns = new Dns();
    byte restran[];
    if(args.length == 0){
      System.out.println("ntmpdpdpddpdpdp");
      restran = dns.createLabel("www.lifl.fr");
      
    }
    else{
      restran = dns.createLabel(args[0]);
    }
	byte[] msgenvoy = dns.addtoData(restran);
  DatagramSocket ds = dns.sendDns(msgenvoy);
  DatagramPacket dp = dns.receiveDns(ds);


/* affichage complet du packet recu (pas tres lisible ...) */
byte[] rec = dp.getData();
System.out.println(" longueur du message recu : " + dp.getLength());

/* affichage des bytes (et ascii associes) */
for(int i = 0; i < dp.getLength(); i++) {
    /* NOTE :
     *   utiliser au besoin l'expression "rec[i] & 0xff" pour obtenir une valeur
     *   entre 0 et 255 (et pas entre -128 et +127).
     */

    System.out.print(" "+String.format("%02X",rec[i]));

    if (((i+1)%16 == 0) || (i+1 == dp.getLength())) {

        /* ceci pour afficher les caracteres ascii aprec l'hexa */
        /* >>> */
        System.out.print("\t");

        for (int j = 0 ; j < ((i+1) & 15); j++) {
            System.out.print("     ");
        }
        for (int j = i & ~15; j <= i; j++) {
            if (rec[j] > 31) {
                System.out.print((char)rec[j]);
            } else {
                System.out.print(".");
            }
        }
        /* <<< */

        System.out.println("");
    }
}
/*isolement de l'adresse IP*/
    System.out.println(dns.addIP(rec));
    
    try {
        long ipp = Dns.ipToInt("www.lifl.fr");
        System.out.println();
        System.out.println("exemple IP sous forme d'entier pour la question 5, ici pour www.lifl.fr: "+ipp);
    } catch (Exception e) {
        System.err.println("[error] :" +  e.getMessage());
        return;
    }

}



  public byte[] createLabel(String label){

    int i,j;
    i=0;
    j=1;

    String[] substr = label.split("\\.");

    byte[] res = new byte[label.length()+1];
    byte[] length= new byte[1];
    for(String str : substr){

      length[0] = (byte)str.length();
      byte[] msg = str.getBytes();



      System.arraycopy(length, 0, res, i, length.length);
      System.arraycopy(msg, 0, res, j, msg.length);

      i += msg.length+1;
      j += msg.length+length.length;
    }


    return res;
  }

public String addIP(byte[] rec){
  int cardom;
  int ind =12;
  int cpt =rec[ind];
  String domname = ""; 
  int j;
  while (cpt!= 0){
    for (j = ind+1;j<=(ind+cpt);j++){
          cardom = rec[j];
          domname += (char)cardom;
        }
      ind = j;
      cpt = rec[ind]; 
      if (cpt != 0){
        domname += ".";  
      }
  }
   
    InetAddress recep;
    try {
        recep = InetAddress.getByName(domname);
    } catch (Exception e) {
        System.out.println("[error] :" +  e.getMessage());
        return "";
    }
    System.out.println();
    return "l'adresse IP de nom de domaine "+ domname +" est " +recep.getHostAddress();
}

public DatagramSocket sendDns(byte[] msgenvoy){
  /* 1) Get DNS server address ... by DNS ... (??!)  */
  System.out.print(" get inetaddress by name ... ");
  InetAddress destination;
  try {
      destination = InetAddress.getByName("reserv1.univ-lille1.fr");
  } catch (Exception e) {
      System.out.println("[error] :" +  e.getMessage());
      return null;
  }
  System.out.println("[ok]");

/* 2) creation d'un DatagramPacket pour l'envoi de la question DNS */
  System.out.println(" preparing  datagrampacket, message size : "+msgenvoy.length  );
  DatagramPacket dp = new DatagramPacket(msgenvoy,msgenvoy.length,destination,53);

  /* 3) creation d'un DatragramSocket (port au choix ) */
  System.out.print(" create datagram socket  ... ");
  DatagramSocket ds ;
  try {
      ds = new DatagramSocket() ;
  } catch (Exception e) {
      System.out.println("[error] :" +  e.getMessage());
      return null;
  }
  System.out.println("[ok]");

  /* 4) et envoi du packet */
  System.out.println(" send datagram ... ");
  try {
      ds.send(dp);
  } catch (Exception e) {
      System.out.println("[outor] :" +  e.getMessage());
      ds.close();
      return null;
  }
  System.out.println("[ok]");
  return ds;
}
public DatagramPacket receiveDns(DatagramSocket ds){
  /* 5) reception du packet */
DatagramPacket dp = new DatagramPacket(new byte[512],512);
System.err.print(" reception du message ... ");
try {
    ds.receive(dp);
} catch (Exception e) {
    System.err.println("[error] :" +  e.getMessage());
    return null;
}
System.err.println("[ok]");
return dp;
}

public byte[] addtoData(byte[] restran){
  byte[] message1 = {(byte) 0x08, (byte) 0xbb,  (byte) 0x01, (byte) 0x00,/* a) 12 octets d'entete : identifiant de requete parametres [RFC1035, 4.1.1]*/
            (byte) 0x00, (byte) 0x01,  (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00,  (byte) 0x00, (byte) 0x00};


  byte[] message2={(byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x01};
  byte[] msgenvoy = new byte[message1.length+message2.length+restran.length];
  System.arraycopy(message1, 0, msgenvoy, 0, message1.length);
  System.arraycopy(restran, 0, msgenvoy, message1.length, restran.length);
  System.arraycopy(message2, 0, msgenvoy, message1.length+restran.length, message2.length);
  return msgenvoy;
}

public static long ipToInt(String adresse) throws IOException{
  long res=0;
  InetAddress recep;
    try {
        recep = InetAddress.getByName(adresse);
    } catch (Exception e) {
        System.out.println("[error] :" +  e.getMessage());
        return -1;
    }
    String addIP = recep.getHostAddress();
    String tab = addIP.replace(".","");
    for(int i=0;i<tab.length();i++){
      res *= 10;
      res+= Character.getNumericValue(tab.charAt(i));
    }

    
return res;
}
}
