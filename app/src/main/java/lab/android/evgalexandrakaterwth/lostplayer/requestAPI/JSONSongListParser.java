package lab.android.evgalexandrakaterwth.lostplayer.requestAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lab.android.evgalexandrakaterwth.lostplayer.model.SongItem;

/**
 * Created by ekaterina on 26.07.2015.
 */
public class JSONSongListParser {
    private static final String INDEX_PROPERTY_NAME = "index";
    private static final String FILE_PROPERTY_NAME = "fileName";

    public static List<SongItem> parse(String jsonString) throws JSONException {
        List<SongItem> items = new ArrayList<>();
        JSONArray songList = new JSONArray(jsonString);
        for (int i = 0; i < songList.length(); i++) {
            JSONObject songJSONObject = songList.getJSONObject(i);
            items.add(parseItem(songJSONObject));
        }
        return items;
    }

    private static SongItem parseItem(JSONObject songObject) throws JSONException {
        long songIndex = songObject.getLong(INDEX_PROPERTY_NAME);
        String fileName = songObject.getString(FILE_PROPERTY_NAME);
        return new SongItem(songIndex, fileName);
    }
}
