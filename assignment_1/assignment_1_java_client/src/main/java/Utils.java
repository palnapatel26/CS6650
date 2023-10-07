import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {
    public static List POST_DATA;
    public static List GET_DATA;
    public static AtomicInteger successCount = new AtomicInteger(0);
    public static AtomicInteger failureCount = new AtomicInteger(0);

    public static void init(String[] args) {
        // each task will have a get and a post that's why *2
        POST_DATA = Collections.synchronizedList(new ArrayList<>(Constants.NUM_TASKS * 2));
        GET_DATA = Collections.synchronizedList(new ArrayList<>(Constants.NUM_TASKS * 2));
    }

    public static void responseTimeStats(List<long[]> requestType) {
        // Calculate mean response time
        long totalResponseTime = 0;
        long minResponseTime = Long.MAX_VALUE;
        long maxResponseTime = Long.MIN_VALUE;
        List<Long> responseTimes = new ArrayList<>();

        for (long[] entry : requestType) {
            long latency = entry[1];

            totalResponseTime += latency;
            minResponseTime = Math.min(minResponseTime, latency);
            maxResponseTime = Math.max(maxResponseTime, latency);
            responseTimes.add(latency);
        }

        int dataSize = responseTimes.size();

        // Calculate median response time
        Collections.sort(responseTimes);
        long medianResponseTime;
        if (dataSize % 2 == 0) {
            medianResponseTime = (responseTimes.get(dataSize / 2 - 1) + responseTimes.get(dataSize / 2)) / 2;
        } else {
            medianResponseTime = responseTimes.get(dataSize / 2);
        }

        // Calculate p99 (99th percentile) response time
        int p99Index = (int) Math.ceil(dataSize * 0.99) - 1;
        long p99ResponseTime = responseTimes.get(p99Index);

        // Calculate mean response time
        double meanResponseTime = (double) totalResponseTime / dataSize;

        // Print the results
        System.out.println("Mean Response Time: " + meanResponseTime + " ms");
        System.out.println("Median Response Time: " + medianResponseTime + " ms");
        System.out.println("99th Percentile Response Time (p99): " + p99ResponseTime + " ms");
        System.out.println("Minimum Response Time: " + minResponseTime + " ms");
        System.out.println("Maximum Response Time: " + maxResponseTime + " ms");
    }

}
