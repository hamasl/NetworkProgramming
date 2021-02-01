import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpServer {
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
            String equation = new String(buffer);
            if ("e".equals(equation.trim())) {
               keepGoing = false;
               System.out.println("Client requested to shut down conversation");
            } else {
               String answer;
               String[] splitInput = equation.split(" ");
               char operand = splitInput[1].charAt(0);
               if (operand == '+') {
                  answer = splitInput[0] + " + " + splitInput[2] + " = "
                        + (Double.parseDouble(splitInput[0]) + Double.parseDouble(splitInput[2]));
               } else if (operand == '-') {
                  answer = splitInput[0] + " - " + splitInput[2] + " = "
                        + (Double.parseDouble(splitInput[0]) - Double.parseDouble(splitInput[2]));
               } else {
                  answer = "Operand has to be either + or -";
               }
               InetAddress address = packet.getAddress();
               int port = packet.getPort();
               buffer = answer.getBytes();
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