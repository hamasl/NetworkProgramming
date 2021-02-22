import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
   public static void main(String[] args) {
      final int PORT = 11111;
      System.out.println("Server starting up");
      // Try with resources so that all connections are closed at the end
      try (ServerSocket ss = new ServerSocket(PORT); Socket s = ss.accept()) {
         PrintWriter pw = new PrintWriter(s.getOutputStream());
         BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
         System.out.println("A connection has been made");

         String toBeAdded = "";
         String input = "";
         while ((toBeAdded = br.readLine()) != null && !toBeAdded.isEmpty()) {
            input += toBeAdded + "\n";
         }

         String outputString = "HTTP/1.1 200 OK\nContent-Type: text/html; charset=utf-8\n\n<!DOCTYPE html>\n<html><body>\n<h1>Hilsen. Du har koblet deg opp til min enkle web-tjener</h1>\nHeader fra klienten er:\n<ul>\n";

         String[] splitInput = input.split("\n");
         for (String string : splitInput)
            outputString += "<li>" + string + "</li>\n";
         outputString += "</ul>\n</body></html>";
         pw.println(outputString);
         pw.flush();
      } catch (IOException ioe) {
         System.out.println("Server could not be initialized:\nError message:\n" + ioe.getMessage());
      }
      System.out.println("Server is shutting down");
   }
}