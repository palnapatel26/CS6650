import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaClient {
    public static void main(String[] args) throws InterruptedException {
        // record start time
        long startTime = System.currentTimeMillis();

        // Create thread pool
        ExecutorService threadPool = Executors.newFixedThreadPool(Constants.NUM_THREADS);
        CountDownLatch latch = new CountDownLatch(Constants.NUM_THREADS);

        // Submit tasks to thread pool
        for (int i = 0; i < Constants.NUM_THREADS; i++) {
            // Loop through
            // for(i = 0; i < numThreadGroupSize; i++) {
                // new Thread()
            //}
            // wait 2 seconds
        }

        // Wait for all threads to finish
        latch.await();

        // record end time
        long endTime = System.currentTimeMillis();
        long totalExecutionTime = endTime - startTime;

        long throughPut = Constants.NUM_TASKS / (totalExecutionTime / 1000);

        // Shut down the thread pool
        threadPool.shutdown();

        System.out.println(
                "Number of Tasks: " + Constants.NUM_TASKS + "\n"
                        + "Number of threads: " + Constants.NUM_THREADS + "\n"
                        + "Total run time (wall time) in ms: " + totalExecutionTime + "\n"
                        + "Total throughput in requests per second: " + throughPut
        );
    }

}
