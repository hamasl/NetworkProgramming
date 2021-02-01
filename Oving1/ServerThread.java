import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerThread extends Thread {
   private final Socket SOCKET;

   public ServerThread(Socket s) {
      this.SOCKET = s;
   }

   @Override
   public void run() {

      try (BufferedReader br = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));
            PrintWriter pw = new PrintWriter(SOCKET.getOutputStream())) {
         pw.println("Connection with server at address: " + SOCKET.getLocalAddress().toString() + " has been made.");

         String input = "";
         while (!"e".equals(input)) {
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
      try {
         SOCKET.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}