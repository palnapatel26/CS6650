import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static ExecutorService launchThreadPool(int numThreads, int numRequests, CountDownLatch latch) {
        ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        for(int i = 0; i < numThreads; i++) {
            threadPool.submit(new AlbumThread(latch, numRequests));
        }
        return threadPool;
    }
    public static void main(String[] args) throws InterruptedException {
        // arguments
        Constants.init(args);

        // change countdownlatch to 10
        CountDownLatch initializationPhaseLatch = new CountDownLatch(1);

        // Create thread pool
        // change threads to 10 and numRequests to 100
        ExecutorService initializationPhaseThreadPool = launchThreadPool(1, 1,
                initializationPhaseLatch);

        initializationPhaseLatch.await();

        initializationPhaseThreadPool.shutdown();

//        // record start time
//        long startTime = System.currentTimeMillis();
//
//        CountDownLatch latch = new CountDownLatch(Constants.NUM_THREADS);
//
//        ExecutorService[] threadPools = new ExecutorService[Constants.NUM_THREAD_GROUPS];
//
//        // Loop through all the threads
//        for (int i = 0; i < Constants.NUM_THREAD_GROUPS; i++) {
//            // Create thread pool
//            threadPools[i] = launchThreadPool(Constants.THREAD_GROUP_SIZE, Constants.NUM_TASKS_PER_THREAD, latch);
//
//            // wait 2 seconds
//            Thread.sleep(Constants.DELAY * 1000);
//
//        }
//
//        // Wait for all threads to finish
//        latch.await();
//
//        // record end time
//        long endTime = System.currentTimeMillis();
//        long totalExecutionTime = endTime - startTime;
//
//        long throughPut = (Constants.NUM_TASKS)
//                / (totalExecutionTime / 1000);
//
//        // Shut down the thread pool
//        for(ExecutorService threadPool : threadPools) {
//            threadPool.shutdown();
//        }
//
//        System.out.println(
//                "Number of Tasks: " + Constants.NUM_TASKS + "\n"
//                        + "Number of threads: " + Constants.NUM_THREADS + "\n"
//                        + "Total run time (wall time) in ms: " + totalExecutionTime + "\n"
//                        + "Total throughput in requests per second: " + throughPut
//        );
    }
}
