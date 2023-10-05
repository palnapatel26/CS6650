public class Album {
    private String image;
    private Profile profile;

    public Album(String image, Profile profile) {
        this.image = image;
        this.profile = profile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
