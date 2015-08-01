package lab.android.evgalexandrakaterwth.lostplayer.model;

public class SongItem {

    private long id;
    private String title;

    public SongItem(long songID, String songTitle) {
        id = songID;
        title = songTitle;
    }

    public long getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

}
