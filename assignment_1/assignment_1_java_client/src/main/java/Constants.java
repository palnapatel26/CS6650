public class Constants {
    public static String SERVER_IP = "127.0.0.1";
    public static int THREAD_GROUP_SIZE = 100;
    public static int NUM_THREAD_GROUPS = 10;
    public static int NUM_TASKS;
    public static int NUM_THREADS;
    public static int NUM_TASKS_PER_THREAD = 1000;
    public static int DELAY = 2;

    public static void init(String[] args) {
        THREAD_GROUP_SIZE = Integer.valueOf(args[0]);
        NUM_THREAD_GROUPS = Integer.valueOf(args[1]);
        DELAY = Integer.valueOf(args[2]);
        SERVER_IP = args[3];
        NUM_THREADS = THREAD_GROUP_SIZE * NUM_THREAD_GROUPS;
        NUM_TASKS = NUM_THREADS * NUM_TASKS_PER_THREAD;
    }
}
