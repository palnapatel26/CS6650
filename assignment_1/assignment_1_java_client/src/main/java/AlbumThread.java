import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsBody;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.model.ImageMetaData;

import java.io.File;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class AlbumThread implements Runnable{
    private final CountDownLatch latch;
    private final int numRequests;

    public AlbumThread(CountDownLatch latch, int numRequests) {
        this.latch = latch;
        this.numRequests = numRequests;
    }

    private ImageMetaData doPost(DefaultApi apiInstance, File image, AlbumsProfile albumsProfile) throws ApiException {
        // Create request


        //record start time
        long startTime = System.currentTimeMillis();

        //TODO: send the request and get response
        ImageMetaData result = apiInstance.newAlbum(image, albumsProfile);

        //record end time
        long endTime = System.currentTimeMillis();

        // the response code from the server
        int responseCode = apiInstance.hashCode();

        long latency = endTime - startTime;

        long[] entry = new long[]{startTime, latency, responseCode};

        Constants.POST_DATA.add(entry);

        return result;

    }

    private void doGet(DefaultApi apiInstance, String albumID) throws ApiException {
        //TODO: create request

        //record start time
        long startTime = System.currentTimeMillis();

        //TODO: send the request and get response
        AlbumInfo albumInfo = apiInstance.getAlbumByKey(albumID);

        //record end time
        long endTime = System.currentTimeMillis();
        // the response code from the server
        int responseCode = 0;

        long latency = endTime - startTime;

        long[] entry = new long[]{startTime, latency, responseCode};

        Constants.GET_DATA.add(entry);

    }

    @Override
    public void run() {
        // define the base path which is the url
        final String BASE_PATH = "http://" + Constants.SERVER_IP + ":/8080/assignment_1_server_war";
        DefaultApi apiInstance = new DefaultApi();
        ApiClient apiClient = apiInstance.getApiClient();
        apiClient.setBasePath(BASE_PATH);

        //TODO: post an album


        Random random = new Random();

        File image = new File(".../nmtb.png");

        int randomYear = random.nextInt(2023);
        String year = String.valueOf(randomYear);

        String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";
        StringBuilder artistBuilder = new StringBuilder();
        StringBuilder titleBuilder = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            int albumIndex = random.nextInt(alphaNumeric.length());
            int titleIndex = random.nextInt(alphaNumeric.length());
            char albumChar = alphaNumeric.charAt(albumIndex);
            char titleChar = alphaNumeric.charAt(titleIndex);
            artistBuilder.append(albumChar);
            titleBuilder.append(titleChar);
        }
        String artist = artistBuilder.toString();
        String title = titleBuilder.toString();

        AlbumsProfile albumsProfile = new AlbumsProfile();

        albumsProfile.setArtist(artist);
        albumsProfile.setTitle(title);
        albumsProfile.setYear(year);

        String albumID = null;


        for(int i = 0; i < numRequests; i++) {
            try {
                ImageMetaData result = doPost(apiInstance, image, albumsProfile);
                albumID = result.getAlbumID();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }


        //TODO: get the album I just posted
        for(int i = 0; i < numRequests; i++) {
            try {
                doGet(apiInstance, albumID);
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }



        this.latch.countDown();
    }
}
