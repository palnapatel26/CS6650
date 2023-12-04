import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {
    public static List<Long> post_data;
    public static List<Long> get_data;
    public static List<Long> review_data;
    public static AtomicInteger successCount = new AtomicInteger(0);
    public static AtomicInteger failureCount = new AtomicInteger(0);

    public static void init(String[] args) {
        // each task will have a get and a post that's why *2
        post_data = new ArrayList<>(Constants.NUM_TASKS * 2);
        get_data = new ArrayList<>(Constants.NUM_TASKS * 2);
        review_data = new ArrayList<>(Constants.NUM_TASKS * 2);

    }

    public static void responseTimeStats(List<Long> requestType) {

        int dataSize = requestType.size();
        // Initialize a priority queue for minimum and maximum
        PriorityQueue<Long> minQueue = new PriorityQueue<>();
        PriorityQueue<Long> maxQueue = new PriorityQueue<>(Collections.reverseOrder());

        long totalResponseTime = 0;

        for (long latency : requestType) {
            totalResponseTime += latency;
            minQueue.offer(latency);
            maxQueue.offer(latency);
        }

        // Calculate mean response time
        double meanResponseTime = (double) totalResponseTime / dataSize;

        // Calculate median response time
        long medianResponseTime;
        if (dataSize % 2 == 0) {
            long mid1 = minQueue.poll();
            long mid2 = maxQueue.poll();
            medianResponseTime = (mid1 + mid2) / 2;
        } else {
            medianResponseTime = minQueue.poll();
        }

        // Calculate p99 (99th percentile) response time
        int p99Index = (int) Math.ceil(dataSize * 0.99) - 1;
        PriorityQueue<Long> p99Queue = new PriorityQueue<>(requestType.subList(0, p99Index + 1));
        long p99ResponseTime = p99Queue.poll();

        // Calculate minimum and maximum response times
        long minResponseTime = minQueue.poll();
        long maxResponseTime = maxQueue.poll();

        // Print the results
        System.out.println("Mean Response Time: " + meanResponseTime + " ms");
        System.out.println("Median Response Time: " + medianResponseTime + " ms");
        System.out.println("99th Percentile Response Time (p99): " + p99ResponseTime + " ms");
        System.out.println("Minimum Response Time: " + minResponseTime + " ms");
        System.out.println("Maximum Response Time: " + maxResponseTime + " ms");

    }

}
