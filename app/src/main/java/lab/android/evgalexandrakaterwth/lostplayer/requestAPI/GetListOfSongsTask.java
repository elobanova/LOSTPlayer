package lab.android.evgalexandrakaterwth.lostplayer.requestAPI;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import lab.android.evgalexandrakaterwth.lostplayer.model.SongItem;

/**
 * Created by ekaterina on 26.07.2015.
 */
public class GetListOfSongsTask {
    private OnResponseListener onResponseListener;
    private final ApiPathEnum apiPathEnum;

    public GetListOfSongsTask(ApiPathEnum apiPathEnum) {
        this.apiPathEnum = apiPathEnum;
    }

    public void send() {
        new HttpGetListOfAllSongsTask().execute();
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    private class HttpGetListOfAllSongsTask extends AsyncTask<Void, Void, List<SongItem>> {

        @Override
        protected List<SongItem> doInBackground(Void... args) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(apiPathEnum.getPath());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int code = conn.getResponseCode();
                ResponseEnum responseCode = ResponseEnum.getResponseEnumByCode(code);
                switch (responseCode) {
                    case OK:
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        return JSONSongListParser.parse(sb.toString());
                }
            } catch (MalformedURLException e) {
                onResponseListener.onError(e.getMessage());
            } catch (IOException e) {
                onResponseListener.onError(e.getMessage());
            } catch (JSONException e) {
                onResponseListener.onError(e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<SongItem> songs) {
            super.onPostExecute(songs);
            onResponseListener.onResponse(songs);
        }
    }
}
