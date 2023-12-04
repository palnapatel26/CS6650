import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.util.UUID;
import java.sql.*;



@WebServlet(name = "AlbumServlet", value = "/AlbumServlet")
@MultipartConfig(fileSizeThreshold=1024*1024*10, // 10 MB
        maxFileSize=1024*1024*50, // 50 MB
        maxRequestSize=1024*1024*100) // 100 MB
public class AlbumServlet extends HttpServlet {
    public static String DB_URL = "jdbc:mysql://localhost:3306/albums";
    public static String USER = "root";
    // local password "MyNewPass"
    public static String PASS = "MyNewPass";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // albums/
        response.setContentType("text/plain");
        String urlPath = request.getPathInfo();
        System.out.println("debug print: " + urlPath);

        // check we have a URL!
        if (urlPath == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing parameters");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)
        // [, albumID]

        if (!isUrlValidGet(urlParts)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            String albumID = urlParts[1];
            if (albumID == null) {
                response.getWriter().write("Album ID not found!");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {

                Album album = null;
                try {
                    System.out.println("getting album info from database");
                    album = getAlbum(albumID);
                } catch (ClassNotFoundException | SQLException e) {
                    throw new RuntimeException(e);
                }
                Gson gson = new Gson();
                Profile profile = album.getProfile();
                String stringProfile = gson.toJson(profile, Profile.class);

                PrintWriter out = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.println(stringProfile);
                out.println();
                out.flush();
                response.setStatus(HttpServletResponse.SC_OK);

            }
        }

    }

    private boolean isUrlValidGet(String[] urlParts) {
        // validate the request url path according to the API spec
        // [, albumID]
        if(urlParts.length != 2) {
            return false;
        }
        return true;
    }

    // Helper method to extract the filename from a Part
    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        for (String content : partHeader.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private void insertAlbum(String id, String artist, String title, String year, byte[] image) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Create the albums table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS albums ("
                    + "`id` VARCHAR(255) PRIMARY KEY,"
                    + "`artist` VARCHAR(255),"
                    + "`title` VARCHAR(255),"
                    + "`year` VARCHAR(4),"
                    + "`image` BLOB,"
                    + "`like` INT,"
                    + "`dislike` INT"
                    + ")";
            try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
                preparedStatement.execute();
            }
            int like = 0;
            int dislike = 0;
            String query = "INSERT INTO albums (`id`, `artist`, `title`, `year`, `image`, `like`, `dislike`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, artist);
                preparedStatement.setString(3, title);
                preparedStatement.setString(4, year);
                preparedStatement.setBytes(5, image);
                preparedStatement.setInt(6, like);
                preparedStatement.setInt(7, dislike);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Album getAlbum(String albumID) throws SQLException, ClassNotFoundException {
        Album album = new Album();
        Profile profile = new Profile();
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "SELECT * FROM albums WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, albumID);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    profile.setArtist(rs.getString("artist"));
                    profile.setTitle(rs.getString("title"));
                    profile.setYear(rs.getString("year"));
                    album.setImage(rs.getBytes("image"));
                    album.setLikes(0);
                    album.setDislikes(0);
                    album.setProfile(profile);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return album;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String urlPath = request.getPathInfo();

        // check we have a URL!
        if (urlPath != null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing parameters");
            return;
        }

        //String image = request.getReader().readLine();
        Part imagePart = request.getPart("image");

        Part profilePart = request.getPart("profile");

        if ((profilePart != null && profilePart.getSize() > 0) && imagePart != null && imagePart.getSize() > 0) {
            try {
                // get image data
                byte[] imageData = imagePart.getInputStream().readAllBytes();
                int imageSize = (int) imagePart.getSize();



                // get profile data
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(profilePart.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    System.out.println("sb: " + sb);
                    String jsonString = convertToJsonLikeString(sb.toString());


                    Gson gson = new Gson();
                    Profile profile = gson.fromJson(jsonString, Profile.class);
                    System.out.println("printing profile json");
                    System.out.println(profile.getArtist());
                    System.out.println(profile.getTitle());
                    System.out.println(profile.getYear());


                    //Album album = new Album(imageData, profile);
                    String uniqueID = UUID.randomUUID().toString();
                    //albumDictionary.put(uniqueID, album);

                    //Add database
                    insertAlbum(uniqueID, profile.getArtist(), profile.getTitle(), profile.getYear(), imageData);

                    response.setStatus(HttpServletResponse.SC_OK);
                    JsonObject data = new JsonObject();
                    data.addProperty("albumID", uniqueID);
                    data.addProperty("imageSize", String.valueOf(imageSize));
                    String result = gson.toJson(data);
                    System.out.println(result);
                    response.getWriter().write(result);
                }
            } catch (IOException | SQLException | ClassNotFoundException e) {
                Gson gson = new Gson();
                JsonObject data = new JsonObject();
                data.addProperty("msg", "ERROR: it did not work!");
                String result = gson.toJson(data);
                response.getWriter().write(result);

            }
        }
    }

    public static String convertToJsonLikeString(String input) {
        // First, remove "class AlbumsProfile {" and "}" parts.
        input = input.replace("class AlbumsProfile {", "").replace("}", "").trim();

        // Split the string by spaces followed by words that end with colon
        String[] keyValuePairs = input.split("\\s+(?=[a-zA-Z]+:)");

        // Initialize StringBuilder for JSON-like string.
        StringBuilder jsonLikeBuilder = new StringBuilder();
        jsonLikeBuilder.append("{");

        // Process each key-value pair.
        for (int i = 0; i < keyValuePairs.length; i++) {
            String[] parts = keyValuePairs[i].split(":");

            // Trim whitespace from key and value
            String key = parts[0].trim();
            String value = parts[1].trim();

            // Construct the string: "key": "value"
            jsonLikeBuilder.append("\"").append(key).append("\": \"").append(value).append("\"");

            // If this is not the last item, append a comma.
            if (i < keyValuePairs.length - 1) {
                jsonLikeBuilder.append(", ");
            }
        }

        jsonLikeBuilder.append("}");

        // Convert the StringBuilder to a String and return it.
        return jsonLikeBuilder.toString();
    }

}
