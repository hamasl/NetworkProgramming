import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class SingleThreadedServer {
   private static final int PORT = 6000;

   public static void main(String[] args) {
      try (ServerSocket ss = new ServerSocket(PORT);
            Socket s = ss.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter pw = new PrintWriter(s.getOutputStream())) {
         System.out.println("Connection with client has been made");
         pw.println("Connection with server at address: " + s.getLocalAddress().toString() + " has been made.");
         String input = "";
         while (!"e".equals(input.trim())) {
            pw.println("Enter a math equation to be solved:");
            pw.flush();
            input = br.readLine();
            String[] splitInput = input.split(" ");
            char operation = splitInput[1].charAt(0);
            String answer;
            if (operation == '+') {
               answer = splitInput[0] + " + " + splitInput[2] + " = "
                     + (Double.parseDouble(splitInput[0]) + Double.parseDouble(splitInput[2]));
            } else if (operation == '-') {
               answer = splitInput[0] + " - " + splitInput[2] + " = "
                     + (Double.parseDouble(splitInput[0]) - Double.parseDouble(splitInput[2]));
            } else
               answer = "Operation has to be + or -";
            pw.println(answer);
            pw.flush();
            input = br.readLine();
         }
      } catch (IOException ioe) {
         System.out.println("An IOException with message: " + ioe.getMessage() + " occured.");
      } catch (NumberFormatException ne) {
         System.out.println("A NumberFormatException with message: " + ne.getMessage() + " occured.");
      }
      System.out.println("Server shutting down");
   }
}