import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.UUID;

@WebServlet(name = "AlbumServlet", value = "/AlbumServlet")
@MultipartConfig(fileSizeThreshold=1024*1024*10, // 10 MB
        maxFileSize=1024*1024*50, // 50 MB
        maxRequestSize=1024*1024*100) // 100 MB
public class AlbumServlet extends HttpServlet {
    private Profile profile1 = new Profile("Sex Pistols","Never Mind The Bollocks!", "1977");
    private Profile profile2 = new Profile("Drake","Take Care", "2011");
    private Profile profile3 = new Profile("J. Cole","Born Sinner", "2013");
    private byte[] image1 = new byte[] {10, 20, 15};
    private byte[] image2 = new byte[] {10, 20, 15};
    private byte[] image3 = new byte[] {10, 20, 15};


    private Album album1 = new Album(image1, profile1);
    private Album album2 = new Album(image2, profile2);
    private Album album3 = new Album(image3, profile3);

    private HashMap<String, Album> albumDictionary = new HashMap<String, Album>(){{
        put("1", album1);
        put("2", album2);
        put("3", album3);
    }};
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // albums/
        response.setContentType("text/plain");
        String urlPath = request.getPathInfo();
        System.out.println("debug print: " + urlPath);

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
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
            Album album = albumDictionary.get(albumID);
            if (albumID == null) {
                response.getWriter().write("Album ID not found!");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                Gson gson = new Gson();
                Profile profile = album.getProfile();
                String profileString = gson.toJson(profile);

                PrintWriter out = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.print(profileString);
                out.println();
                out.flush();
                response.setStatus(HttpServletResponse.SC_OK);

            }
        }

    }

    private boolean isUrlValidGet(String[] urlParts) {
        // TODO: validate the request url path according to the API spec
        // [, albumID]
        if(urlParts.length != 2) {
            return false;
        }
        try {
            Integer.valueOf(urlParts[1]);
        } catch (NumberFormatException e) {
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String urlPath = request.getPathInfo();
        System.out.println("urlPath: " + urlPath);

        // check we have a URL!
        if (urlPath != null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing parameters");
            return;
        }

        System.out.println("reaching this point");
        //String image = request.getReader().readLine();
        Part imagePart = request.getPart("image");

        Part profilePart = request.getPart("profile");

        if ((profilePart != null && profilePart.getSize() > 0) && imagePart != null && imagePart.getSize() > 0) {
            try {
                // get image data
                //byte[] imageData = new byte[(int) imagePart.getSize()];
                byte[] imageData = new byte[] {10, 20, 15};
                int imageSize = (int) imagePart.getSize();

                System.out.println(imageData.length);

                // get profile data
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(profilePart.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    System.out.println(sb);

                    Gson gson = new Gson();
                    //Profile profile = gson.fromJson(sb.toString(), Profile.class);


                    //Album album = new Album(imageData, profile);
                    String uniqueID = UUID.randomUUID().toString();
                    //albumDictionary.put(uniqueID, album);

                    response.setStatus(HttpServletResponse.SC_OK);
                    JsonObject data = new JsonObject();
                    data.addProperty("albumID", uniqueID);
                    data.addProperty("imageSize", String.valueOf(imageSize));
                    String result = gson.toJson(data);
                    System.out.println(result);
                    response.getWriter().write(result);
                }
            } catch (IOException e) {
                Gson gson = new Gson();
                JsonObject data = new JsonObject();
                data.addProperty("msg", "ERROR: it did not work!");
                String result = gson.toJson(data);
                response.getWriter().write(result);

            }
        }
    }
}
