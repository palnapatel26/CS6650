import java.util.concurrent.CountDownLatch;

public class AlbumThread implements Runnable{
    private final CountDownLatch latch;
    private final int numRequests;

    public AlbumThread(CountDownLatch latch, int numRequests) {
        this.latch = latch;
        this.numRequests = numRequests;
    }


    @Override
    public void run() {
        // define the base path which is the url
        final String BASE_PATH = "http://" + Constants.SERVER_IP + ":/8080/assignment_1_server_war";

        // To do: post an album

        // To do: get the album I just posted




        this.latch.countDown();
    }
}
