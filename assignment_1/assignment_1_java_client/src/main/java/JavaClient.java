import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaClient {
    public static int threadGroupSize;
    public static int numThreadGroups;
    public static int delay;
    public static String iPAddr;
    public static void main(String[] args) throws InterruptedException {
        // arguments
        threadGroupSize = Integer.valueOf(args[1]);
        numThreadGroups = Integer.valueOf(args[2]);
        delay = Integer.valueOf(args[3]);
        iPAddr = args[4];






//        // record start time
//        long startTime = System.currentTimeMillis();
//
//        // Create thread pool
//        ExecutorService threadPool = Executors.newFixedThreadPool(Constants.NUM_THREADS);
//        CountDownLatch latch = new CountDownLatch(Constants.NUM_THREADS);
//
//        // Loop through all the threads
//        for (int i = 0; i < Constants.NUM_THREADS; i++) {
//
//            // Loop through the thread group size
//            // for(i = 0; i < numThreadGroupSize; i++) {
//            // new Thread()
//            //}
//            // wait 2 seconds
//        }
//
//        // Wait for all threads to finish
//        latch.await();
//
//        // record end time
//        long endTime = System.currentTimeMillis();
//        long totalExecutionTime = endTime - startTime;
//
//        long throughPut = Constants.NUM_TASKS / (totalExecutionTime / 1000);
//
//        // Shut down the thread pool
//        threadPool.shutdown();
//
//        System.out.println(
//                "Number of Tasks: " + Constants.NUM_TASKS + "\n"
//                        + "Number of threads: " + Constants.NUM_THREADS + "\n"
//                        + "Total run time (wall time) in ms: " + totalExecutionTime + "\n"
//                        + "Total throughput in requests per second: " + throughPut
//        );
    }
}
