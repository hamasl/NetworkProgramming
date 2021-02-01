import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
   public static void main(String[] args) {
      final int PORT = 6000;
      boolean keepGoing = true;
      Scanner scanner = new Scanner(System.in);

      System.out.println("Enter server address:");
      String address = scanner.nextLine();

      try (Socket s = new Socket(address, PORT);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true)) {
         System.out.println(br.readLine());
         while (keepGoing) {
            System.out.println(br.readLine());

            System.out.println("Enter + to do addition and - to do subtraction");
            char operation = scanner.nextLine().trim().charAt(0);
            System.out.println("Enter the first number:");
            double num1 = scanner.nextDouble();
            scanner.nextLine();
            System.out.println("Enter the second number:");
            double num2 = scanner.nextDouble();
            scanner.nextLine();

            pw.println(num1 + " " + operation + " " + num2);
            pw.flush();

            System.out.println(br.readLine());
            System.out.println("Enter Y to keep going, anything else entered will result in exit:");
            String answer = scanner.nextLine();
            if (answer.length() != 1 || answer.charAt(0) != 'Y') {
               keepGoing = false;
               pw.println("e");

            } else
               pw.println("");
            pw.flush();
         }
      } catch (IOException ioe) {
         System.out.println("An IOException with message: " + ioe.getMessage() + " occured.");
      } catch (InputMismatchException ime) {
         System.out.println("Input was not entered correctly.");
      }

      scanner.close();
   }
}