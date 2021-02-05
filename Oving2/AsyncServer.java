import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncServer {
   private static AsynchronousSocketChannel channel;
   private static AtomicBoolean isDone = new AtomicBoolean(false);

   public static void main(String[] args) {
      final String LOCAL_IP = "127.0.0.1";
      final int PORT = 32432;

      System.out.println("Server starting up\n");
      try {

         AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
         server.bind(new InetSocketAddress(LOCAL_IP, PORT));

         CompletableFuture<Boolean> promise = CompletableFuture.supplyAsync(() -> {
            try {
               channel = server.accept().get();
               return true;
            } catch (InterruptedException | ExecutionException e) {
               e.printStackTrace();
               return false;
            }
         });

         ByteBuffer buffer = ByteBuffer.allocate(500);
         promise.thenAccept(completed -> {

            if (!completed)
               promise.cancel(true);

            String message = "Write me a message and I will return it.\n";
            ByteBuffer bufferedMessage = ByteBuffer.wrap(message.getBytes());
            channel.write(bufferedMessage);
         }).thenRun(() -> {
            try {
               channel.read(buffer).get();
            } catch (InterruptedException | ExecutionException e) {
               e.printStackTrace();
            }
         }).thenRun(() -> {
            buffer.flip();
            try {
               channel.write(buffer).get();
            } catch (InterruptedException | ExecutionException e) {
               e.printStackTrace();
            }
            buffer.clear();
         }).thenRun(() -> {
            try {
               channel.close();
               server.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }).thenRun(() -> {
            isDone.set(true);
         });

         // Just to demonstrate async
         System.out.println("These numbers are used to demonstrate that async works:");
         for (int i = 0; i < 10; i++) {
            System.out.println(i);
         }

         while (!isDone.get())
            ;
      } catch (IOException e) {
         e.printStackTrace();
      }
      System.out.println("\nServer shutting down");
   }
}