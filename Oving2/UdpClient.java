import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UdpClient {
   public static void main(String[] args) {

      final int SERVER_PORT = 7987;
      boolean keepGoing = true;
      Scanner scanner = new Scanner(System.in);

      System.out.println("Enter server address:");

      try (DatagramSocket ds = new DatagramSocket()) {
         InetAddress address = InetAddress.getByName(scanner.nextLine());
         while (keepGoing) {
            System.out.println("Enter + to do addition and - to do subtraction");
            char operation = scanner.nextLine().trim().charAt(0);
            System.out.println("Enter the first number:");
            double num1 = scanner.nextDouble();
            scanner.nextLine();
            System.out.println("Enter the second number:");
            double num2 = scanner.nextDouble();
            scanner.nextLine();
            String message = num1 + " " + operation + " " + num2 + "\n";

            byte[] buffer = message.getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
            ds.send(packet);

            buffer = new byte[350];
            packet = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
            ds.receive(packet);
            System.out.println(new String(buffer));
            System.out.println("Enter Y to keep going, anything else entered will result in exit:");
            String answer = scanner.nextLine();
            if (answer.length() != 1 || answer.charAt(0) != 'Y') {
               keepGoing = false;
               buffer = "e\n".getBytes();
               packet = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
               ds.send(packet);
            }
         }

      } catch (UnknownHostException uhe) {
         System.out.println("The host entered is unknown.");
      } catch (InputMismatchException ime) {
         System.out.println("Input was not entered correctly.");
      } catch (SocketException se) {
         System.out.println("SocketException with message: " + se + " occured");
      } catch (IOException ioe) {
         System.out.println("IOException with message: " + ioe + " occured");
      }

      scanner.close();
   }
}