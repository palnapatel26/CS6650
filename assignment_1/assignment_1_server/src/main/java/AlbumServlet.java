import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Objects;

@WebServlet(name = "AlbumServlet", value = "/AlbumServlet")
public class AlbumServlet extends HttpServlet {
    private Album album1 = new Album("Sex Pistols","Never Mind The Bollocks!", "1977");
    private Album album2 = new Album("Drake","Take Care", "2011");
    private Album album3 = new Album("J. Cole","Born Sinner", "2013");
    private HashMap<String, Album> dictionary = new HashMap<String, Album>(){{
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
        // [, album, albumID]

        if (!isUrlValidGet(urlParts)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            String albumID = urlParts[1];
            Album album = dictionary.get(albumID);
            if (album == null) {
                response.getWriter().write("Album ID not found!");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                Gson gson = new Gson();
                String albumString = gson.toJson(album);

                PrintWriter out = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.print(albumString);
                out.flush();
                response.getWriter().write("Album ID found!");
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        String urlPath = request.getPathInfo();

        // check we have a URL!
        if (!(urlPath == null || urlPath.isEmpty())) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing parameters");
            return;
        }

        StringBuilder sb = new StringBuilder();
        String s;
        String id = request.getReader().readLine();
        while((s = request.getReader().readLine()) != null) {
            sb.append(s);
        }
        Gson gson = new Gson();
        Album album = gson.fromJson(sb.toString(), Album.class);
        dictionary.put(id, album);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("It works!!");
    }
}
