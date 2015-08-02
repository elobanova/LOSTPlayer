package lab.android.evgalexandrakaterwth.lostplayer.funf;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.mit.media.funf.Schedule;
import edu.mit.media.funf.config.Configurable;
import edu.mit.media.funf.probe.Probe.Base;
import edu.mit.media.funf.probe.Probe.ContinuousProbe;
import edu.mit.media.funf.probe.Probe.Description;
import edu.mit.media.funf.probe.Probe.DisplayName;
import edu.mit.media.funf.probe.builtin.SensorProbe;

/**
 * Created by evgenijavstein on 09/07/15.
 * Probe getting current weather conditions form api.openweathermap.org.
 * The data is broadcasted to the <code>WriteQueueAction</code> instance.
 */
@DisplayName("Weather Probe")
@Description("Receives weather information from third party API")
@Schedule.DefaultSchedule(interval = SensorProbe.DEFAULT_PERIOD, duration = SensorProbe.DEFAULT_DURATION)
public class WeatherProbe extends Base implements ContinuousProbe {

    @Configurable
    String maxWaitTime;

    private final static String API_KEY = "4f5b104383e47d2de0b31d4ad860de0c";

    Runnable weatherFetcherTask = null;
    private String url = "http://api.openweathermap.org/data/2.5/weather?lat=%d&lon=%d&APPID=" + API_KEY;


    @Override
    protected void onEnable() {

        super.onEnable();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(WeatherProbe.class.getCanonicalName(), "---------- wstarted -------------");


        weatherFetcherTask = new Runnable() {
            @Override
            public void run() {
                int lat = 0, lon = 0;
                LocationManager locService = (LocationManager) getContext().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                //we are capturing location anyway with concurrent probe use passive provider
                //is a workaround because is is not possible to scedule  this probe to be done after the simpplelocation probe
                //which is only possible for filters
                Location location = locService.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if (location != null) {//null is returned if GPS
                    lat = (int) location.getLatitude();//we do not need double because of the API
                    lon = (int) location.getLongitude();
                    url = String.format(url, lat, lon);

                }

                String response = null;
                if (lon != 0 && lat != 0)//we assume nobody is interested in weather condition for lat 0.0 and long 0.0
                    response = request(url);


                if (response != null && !response.equals("")) {
                    JsonParser parser = new JsonParser();
                    JsonObject o = (JsonObject) parser.parse(response);
                    sendData(o);
                }

            }
        };

        getHandler().post(weatherFetcherTask);

    }

    @Override
    protected void onStop() {
        stop();//causes onDataCompleted to be called in WriteQueueAction, will be called if a duration set

    }

    @Override
    protected void onDisable() {
        disable();

    }

    private String request(String urlString) {

        StringBuilder chaine = new StringBuilder("");
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(Integer.parseInt(maxWaitTime) * 1000);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }

        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();
        }
        return chaine.toString();
    }
}
