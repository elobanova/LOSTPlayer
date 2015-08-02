package lab.android.evgalexandrakaterwth.lostplayer.requestAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import lab.android.evgalexandrakaterwth.lostplayer.context.ContextFeatures;
import lab.android.evgalexandrakaterwth.lostplayer.model.SongItem;


/**
 * Created by evgenijavstein on 26/07/15.
 */
public class RecommendationPostRequest {
    public static final String USER = "user";
    public static final String CONTEXT = "context";
    public static final String USERID = "userid";

    private Context context;
    private OnResponseListener onResponseListener;
    private final String path;

    public RecommendationPostRequest(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    public void send(ContextFeatures userContext, String user) {
        new HttpPostLearnTask(user).execute(userContext);
    }

    private class HttpPostLearnTask extends AsyncTask<ContextFeatures, Void, List<SongItem>> {
        private String user;

        public HttpPostLearnTask(String user) {
            if (user == null)
                this.user = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            else
                this.user = user;
        }

        @Override
        protected List<SongItem> doInBackground(ContextFeatures... args) {
            HttpURLConnection conn = null;
            try {
                JSONObject learningObj = new JSONObject();
                JSONObject userObj = new JSONObject();
                userObj.put(USERID, user);

                learningObj.put(CONTEXT, args[0].getAsJSON());
                learningObj.put(USER, userObj);

                URL url = new URL(path);

                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Content-Length", "" +
                        Integer.toString(learningObj.toString().length()));
                conn.setRequestProperty("Content-Language", "en-US");

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(
                        conn.getOutputStream());
                dataOutputStream.write(learningObj.toString().getBytes("UTF-8"));
                dataOutputStream.flush();
                dataOutputStream.close();

                int status = conn.getResponseCode();
                ResponseEnum responseCode = ResponseEnum.getResponseEnumByCode(status);
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
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<SongItem> songList) {
            super.onPostExecute(songList);
            onResponseListener.onResponse(songList);
        }
    }

}