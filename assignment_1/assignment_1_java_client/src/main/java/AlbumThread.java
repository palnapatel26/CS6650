import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumsProfile;

import java.io.File;
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
        final String BASE_PATH = "http://" + Constants.SERVER_IP + ":8080/assignment_1_server_war";
        DefaultApi apiInstance = new DefaultApi();
        ApiClient apiClient = apiInstance.getApiClient();
        apiClient.setBasePath(BASE_PATH);



        File image = new File("/Users/palnapatel/CS6650/CS6650/assignment_1/assignment_1_java_client/src/main/java/nmtb.png");

        AlbumsProfile albumsProfile = new AlbumsProfile();

        albumsProfile.setArtist("Drake");
        albumsProfile.setTitle("For All the Dogs");
        albumsProfile.setYear("2023");


        for(int i = 0; i < numRequests; i++) {
            int tries = 0;
            while(tries < Constants.MAX_RETRIES) {
                try {
                    apiInstance.newAlbum(image, albumsProfile);
                    apiInstance.getAlbumByKey("1");
                    break;
                } catch (ApiException e) {
                    e.printStackTrace();
                    System.out.println(e.getCode());
                    System.out.println(e.getResponseBody());
                    tries++;
                }

            }

            if(tries < Constants.MAX_RETRIES) {
                Utils.successCount.incrementAndGet();
            } else {
                Utils.failureCount.incrementAndGet();

            }
        }

        this.latch.countDown();

    }
}
