import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Class taken from:
 * http://www.thedevpiece.com/converting-old-java-future-to-completablefuture/
 */
public class CompletablePromiseContext {
   private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();

   public static void schedule(Runnable r) {
      SERVICE.schedule(r, 1, TimeUnit.MILLISECONDS);
   }
}