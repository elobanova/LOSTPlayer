package lab.android.evgalexandrakaterwth.lostplayer.requestAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import lab.android.evgalexandrakaterwth.lostplayer.context.ContextFeatures;


/**
 * Created by evgenijavstein on 26/07/15.
 */
public class LearnPostRequest {
    public static final String FILE_INDEX = "fileIndex";
    public static final String FEED_BACK = "feedBack";
    public static final String USER = "user";
    public static final String CONTEXT = "context";
    public static final String USERID = "userid";

    private Context context;
    private OnResponseListener onResponseListener;
    private final String path;

    public LearnPostRequest(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    /**
     * Send context mapped with song listened too. Use both for negative and positive
     * feedback, both will be passed to ML module on server.
     *
     * @param userContext
     * @param songIndex
     * @param user
     * @param feedBack    true if positive
     */
    public void send(ContextFeatures userContext, int songIndex, String user, boolean feedBack) {
        new HttpPostLearnTask(songIndex, user, feedBack).execute(userContext);
    }

    private class HttpPostLearnTask extends AsyncTask<ContextFeatures, Void, Boolean> {
        private String user;
        private int fileIndex;
        boolean feedBack = false;

        public HttpPostLearnTask(int fileIndex, String user, boolean feedback) {
            if (user == null)
                this.user = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            else
                this.user = user;
            this.fileIndex = fileIndex;
            this.feedBack = feedback;
        }

        @Override
        protected Boolean doInBackground(ContextFeatures... args) {
            HttpURLConnection conn = null;
            try {
                JSONObject learningObj = new JSONObject();
                JSONObject userObj = new JSONObject();
                userObj.put(USERID, user);

                learningObj.put(CONTEXT, args[0].getAsJSON());
                learningObj.put(USER, userObj);
                learningObj.put(FILE_INDEX, fileIndex);
                learningObj.put(FEED_BACK, feedBack);

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
                    case CREATED:
                        return true;
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
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isCreated) {
            super.onPostExecute(isCreated);
            onResponseListener.onResponse(isCreated);
        }
    }

}