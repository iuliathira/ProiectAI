public class Song {

    private String song_id;
    private String title;
    private String release;
    private String artist_name;
    private Integer year;
    private double[] features;


    public Song(String song_id, String title, String release, String artist_name, Integer year) {
        this.song_id = song_id;
        this.title = title;
        this.release = release;
        this.artist_name = artist_name;
        this.year = year;
    }

    public String getSong_id() {
        return song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public double[] getFeatures() {
        return features;
    }

    @Override
    public String toString() {
        return "Song{" +
                "song_id='" + song_id + '\'' +
                ", title='" + title + '\'' +
                ", release='" + release + '\'' +
                ", artist_name='" + artist_name + '\'' +
                ", year=" + year +
                '}';
    }

    public void setFeatures(double[] features) {
        this.features = features;
    }
}
