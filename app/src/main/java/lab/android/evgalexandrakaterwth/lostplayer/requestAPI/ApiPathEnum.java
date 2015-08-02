package lab.android.evgalexandrakaterwth.lostplayer.requestAPI;

/**
 * Created by ekaterina on 26.07.2015.
 */
public enum ApiPathEnum {
    ALL_SONGS("http://192.168.0.11:3000/api/list/"),
    PLAY("http://192.168.0.11:3000/api/play/"),
    GET_RECOMMENDED("http://192.168.0.11:3000/api/list/next");

    private final String path;

    private ApiPathEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}