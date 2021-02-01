import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class MultiThreadServer {
   private static final int PORT = 6000;

   public static void main(String[] args) {
      Scanner s = new Scanner(System.in);
      System.out.println("Enter the number of clients to be served.");
      final int NUMBER_OF_CLIENTS;
      while (!s.hasNextInt()) {
         System.out.println("Enter an integer");
         s.nextLine();
      }
      NUMBER_OF_CLIENTS = s.nextInt();
      s.nextLine();
      s.close();

      ServerThread[] threads = new ServerThread[NUMBER_OF_CLIENTS];
      int clientsConnections = 0;

      try (ServerSocket server = new ServerSocket(PORT)) {
         System.out.println(
               "Server has started running.\nTo shut down server before the numbers of clients to be served are reached, hit Control+C");
         for (clientsConnections = 0; clientsConnections < NUMBER_OF_CLIENTS; clientsConnections++) {
            threads[clientsConnections] = new ServerThread(server.accept());
            threads[clientsConnections].start();
         }
      } catch (IOException ioe) {
         System.out.println("An IOException with message: " + ioe.getMessage() + " occured.");
      }
      System.out.println("Finished with all incomming connections, waiting for all of them to be finished.");
      for (int i = 0; i < clientsConnections; i++) {
         try {
            threads[i].join();
         } catch (InterruptedException e) {
            System.out.println("Not able to join with thread number: " + i);
         }
      }

      System.out.println("All connections finished.\nServer shutting down.");
   }

}