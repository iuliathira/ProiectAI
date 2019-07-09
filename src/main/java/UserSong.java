public class UserSong{
    private String song_id;
    private String user_id;
    private Integer listen_count;

    public UserSong(String song_id, String user_id, Integer listen_count) {
        this.song_id = song_id;
        this.user_id = user_id;
        this.listen_count = listen_count;
    }

    public String getSong_id() {
        return song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Integer getListen_count() {
        return listen_count;
    }

    public void setListen_count(Integer listen_count) {
        this.listen_count = listen_count;
    }
}