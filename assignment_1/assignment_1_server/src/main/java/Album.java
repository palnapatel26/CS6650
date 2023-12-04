public class Album {
    private byte[] image;
    private Profile profile;

    private int likes;
    private int dislikes;

    public Album(byte[] image, Profile profile) {
        this.image = image;
        this.profile = profile;
        this.likes = 0;
        this.dislikes = 0;
    }

    public Album(byte[] image, Profile profile, int likes, int dislikes) {
        this.image = image;
        this.profile = profile;
        this.likes = likes;
        this.dislikes = dislikes;
    }
    public Album(){
        this.image = null;
        this.profile = new Profile();
        this.likes = 0;
        this.dislikes = 0;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
