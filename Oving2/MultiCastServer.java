import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.IOException;
import java.lang.Thread;

public class MultiCastServer {
   static List<DatagramPacket> clients = Collections.synchronizedList(new ArrayList<>());

   public static void main(String[] args) {
      final int SERVER_PORT = 21389;

      Scanner s = new Scanner(System.in);
      try (DatagramSocket ds = new DatagramSocket(SERVER_PORT)) {
         Thread[] threads = new Thread[2];
         threads[0] = new Thread(() -> {
            while (true) {
               byte[] buffer = new byte[400];
               DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
               try {
                  ds.receive(dp);
                  String message = new String(buffer);

                  if ("q".equals(message)) {
                     removeSubscriber(dp.getAddress());
                  } else {
                     clients.add(dp);
                  }
               } catch (IOException ioe) {
                  ioe.printStackTrace();
                  System.out.println("Could not recieve packet");
               }

            }
         });
         threads[1] = new Thread(() -> {
            while (true) {
               System.out.println("Enter a message to be multicasted to all clients (control+c to exit):");
               String message = s.nextLine();

               for (DatagramPacket client : clients) {
                  byte[] buffer = message.getBytes();
                  DatagramPacket dp = new DatagramPacket(buffer, buffer.length, client.getAddress(), client.getPort());
                  try {
                     ds.send(dp);
                  } catch (IOException ioe) {
                     System.out.println("Could not sent packet to address: " + client.getAddress() + " at port: "
                           + client.getPort());
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

      } catch (Exception e) {
         System.out.println("Exception: " + e.getMessage() + " occured");
      }

      s.close();

   }

   private static void removeSubscriber(InetAddress address) {
      List<DatagramPacket> dp = Collections.synchronizedList(
            clients.stream().filter(p -> (!p.getAddress().equals(address))).collect(Collectors.toList()));
      clients = dp;
   }
}