import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.api.LikeApi;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class AlbumThread implements Runnable{
    private final CountDownLatch latch;
    private final int numRequests;

    public AlbumThread(CountDownLatch latch, int numRequests) {
        this.latch = latch;
        this.numRequests = numRequests;
    }


    private void doPost(DefaultApi apiInstance, File image, AlbumsProfile albumsProfile) throws ApiException {


        //record start time
        long startTime = System.currentTimeMillis();

        // Send the request and get response
        apiInstance.newAlbum(image, albumsProfile);


        //record end time
        long endTime = System.currentTimeMillis();

        long latency = endTime - startTime;

        Utils.post_data.add(latency);

    }

    private void doReview(LikeApi likeApi, String albumID) throws ApiException, IOException, TimeoutException {

        //record start time
        long startTime = System.currentTimeMillis();

        // send the request and get response
        likeApi.review("like", albumID);
        likeApi.review("like", albumID);
        likeApi.review("dislike", albumID);

        //record end time
        long endTime = System.currentTimeMillis();

        long latency = endTime - startTime;

        Utils.review_data.add(latency);

    }

    private void doGet(DefaultApi apiInstance, String albumID) throws ApiException {


        //record start time
        long startTime = System.currentTimeMillis();

        // Send the request and get response
        apiInstance.getAlbumByKey(albumID);

        //record end time
        long endTime = System.currentTimeMillis();


        long latency = endTime - startTime;

        Utils.get_data.add(latency);

    }

    @Override
    public void run() {
        // define the base path which is the url
        final String BASE_PATH = Constants.SERVER_IP + ":8080/assignment_1_server_war";
        DefaultApi apiInstance = new DefaultApi();
        LikeApi likeApi = new LikeApi();
        ApiClient apiClient = apiInstance.getApiClient();
        apiClient.setBasePath(BASE_PATH);



        File image = new File("/Users/palnapatel/CS6650/CS6650/assignment_1/assignment_1_client_part_2/src/main/java/nmtb.png");

        AlbumsProfile albumsProfile = new AlbumsProfile();

        albumsProfile.setArtist("Drake");
        albumsProfile.setTitle("For All the Dogs");
        albumsProfile.setYear("2023");

            for (int i = 0; i < numRequests; i++) {
                int tries = 0;
                while (tries < Constants.MAX_RETRIES) {
                    try {
                        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
                        doPost(apiInstance, image, albumsProfile);
                        //doGet(apiInstance, "1");
                        doReview(likeApi, "62432984-7bcf-4c76-a217-73f3d22348a2");
                        break;
                    } catch (ApiException e) {
                        e.printStackTrace();
                        System.out.println(e.getCode());
                        System.out.println(e.getResponseBody());
                        tries++;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (TimeoutException e) {
                        throw new RuntimeException(e);
                    }

                }

                if (tries < Constants.MAX_RETRIES) {
                    Utils.successCount.incrementAndGet();
                } else {
                    Utils.failureCount.incrementAndGet();

                }
            }


        this.latch.countDown();

    }
}
