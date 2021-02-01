import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SerializableUdpServer {
   public static void main(String[] args) {

      final int SERVER_PORT = 7987;
      boolean keepGoing = true;

      try (DatagramSocket ds = new DatagramSocket(SERVER_PORT)) {
         System.out.println("Server is running:");
         while (keepGoing) {
            System.out.println("Waiting for UDP packet...");
            byte[] buffer = new byte[300];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            ds.setReceiveBufferSize(300);
            ds.receive(packet);
            System.out.println("UDP packet recieved");

            Equation e = Equation.deSerialize(buffer);

            if (e == null) {
               keepGoing = false;
               System.out.println("Client requested to shut down conversation");
            } else {
               e.updateAnswer();
               InetAddress address = packet.getAddress();
               int port = packet.getPort();
               buffer = e.serialize(ds.getReceiveBufferSize());
               ds.send(new DatagramPacket(buffer, buffer.length, address, port));
               System.out.println("UDP packet sent");
            }
         }
      } catch (SocketException se) {
         System.out.println("Socket exception with message: " + se.getMessage() + " occured");
      } catch (IOException ioe) {
         System.out.println("IOException with message: " + ioe.getMessage() + " occured");
      }
      System.out.println("Server shutting down");
   }
}