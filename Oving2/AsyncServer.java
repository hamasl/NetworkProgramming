import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncServer {
   private static AsynchronousSocketChannel channel;

   public static void main(String[] args) {
      final String LOCAL_IP = "127.0.0.1";
      final int PORT = 32432;
      try {

         AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
         server.bind(new InetSocketAddress(LOCAL_IP, PORT));
         Future<AsynchronousSocketChannel> future = server.accept();
         CompletableFuture<AsynchronousSocketChannel> promise = new CompletablePromise<>(future)
               .whenComplete((result, failure) -> {
                  if (failure == null) {
                     channel = result;
                  } else
                     System.out.println(failure.getMessage());
               });

         ByteBuffer buffer = ByteBuffer.allocate(500);
         promise.thenRun(() -> {
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
            // Using this so that the server actually exits when finished, since the main
            // "thread" has already exited
            System.exit(0);
         });

         // Just to demonstrate async
         System.out.println("These numbers are used to demonstrate that async works:");
         for (int i = 0; i < 10; i++) {
            System.out.println(i);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

   }
}