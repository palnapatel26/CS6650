public class Profile {
    private String artist;
    private String title;
    private String year;

    public Profile(String artist, String title, String year) {
        this.artist = artist;
        this.title = title;
        this.year = year;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "artist: " + artist + " " + "title: " + title + " " + "year: " + year + " " +
                "]";
    }

}