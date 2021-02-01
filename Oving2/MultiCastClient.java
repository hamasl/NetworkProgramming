import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MultiCastClient {
   private static boolean keepGoing = true;

   public static void main(String[] args) {
      final int SERVER_PORT = 21389;

      Scanner scanner = new Scanner(System.in);

      System.out.println("Enter server address:");
      final String SERVER_ADDRESS = scanner.nextLine();

      try (DatagramSocket ds = new DatagramSocket()) {
         Thread[] threads = new Thread[2];
         threads[0] = new Thread(() -> {
            try {
               InetAddress address = InetAddress.getByName(SERVER_ADDRESS);
               System.out.println("Subscribing to server...");
               byte[] buffer = new byte[256];
               DatagramPacket dp = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
               ds.send(dp);
               System.out.println("Subscribed to server.");

               while (keepGoing) {
                  buffer = new byte[256];
                  dp = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
                  ds.receive(dp);
                  System.out.println(new String(buffer));
               }
            } catch (UnknownHostException uhe) {
               System.out.println("Could not find host with that address");
            } catch (IOException ioe) {
               System.out.println("IOException occered");
            }

         });

         threads[1] = new Thread(() -> {
            while (keepGoing) {

               System.out.println("To exit prgram enter q");
               String command = scanner.nextLine();
               if ("q".equals(command)) {
                  keepGoing = false;
                  byte[] buffer = new byte[400];
                  try {
                     DatagramPacket dp = new DatagramPacket(buffer, buffer.length,
                           InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
                     ds.send(dp);
                  } catch (UnknownHostException uhe) {
                     System.out.println("Could not find host with that address");
                  } catch (IOException ioe) {
                     System.out.println("IOException occured");
                  }

               }
            }
         });

         for (Thread thread : threads) {
            thread.start();
         }
         for (Thread thread : threads) {
            try {
               thread.join();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
         scanner.close();
      } catch (IOException ioe) {
         System.out.println("IOException with message: " + ioe.getMessage() + " occured");
      }

   }
}