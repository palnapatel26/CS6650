import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

@WebServlet(name = "ReviewServlet", value = "/ReviewServlet")
public class ReviewServlet extends HttpServlet {
    public static String DB_URL = "jdbc:mysql://localhost:3306/albums";
    public static String USER = "root";
    // local password "MyNewPass"
    public static String PASS = "MyNewPass";
    private final static int NUM_MESSAGES_PER_THREAD = 10;

    public final String QUEUE_NAME = "reviewQueue";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String urlPath = request.getPathInfo();
        System.out.println("URL: " + urlPath);

        // check we have a URL!
        if (urlPath == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing parameters");
            return;
        }

        String[] urlParts = urlPath.split("/");
        System.out.println(Arrays.asList(urlParts));
        String likeOrNot = urlParts[1];
        String albumID = urlParts[2];

        if (!isReviewUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid parameters\"}");
        } else {
            try {
                updateAlbumReview(likeOrNot, albumID);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\":\"Album review updated successfully\"}");
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Internal server error\"}");
            }
        }

    }

    private void updateAlbumReview(String likeOrNot, String albumID) throws SQLException, ClassNotFoundException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
                try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
                    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                    String message = likeOrNot + " " + albumID;
                    channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                    System.out.println("Sent message: " + message);
                    System.out.println(" [All Messages  Sent '" );
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
    }


    private boolean isReviewUrlValid(String[] urlParts) {
        if (urlParts.length != 3) {
            return false; // URL should have exactly 4 parts
        }

        String likeOrNot = urlParts[1];

        // Check if likeOrNot is either "like" or "dislike"
        if (!("like".equals(likeOrNot) || "dislike".equals(likeOrNot))) {
            System.out.println("not valid url");
            return false;
        }
        System.out.println(" valid url");
        return true;
    }
}
