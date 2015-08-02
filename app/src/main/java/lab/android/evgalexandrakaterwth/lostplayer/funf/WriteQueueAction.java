package lab.android.evgalexandrakaterwth.lostplayer.funf;

import android.util.Log;

import com.google.gson.JsonElement;


import java.util.ArrayList;
import java.util.Date;

import edu.mit.media.funf.config.RuntimeTypeAdapterFactory;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.util.LogUtil;
import lab.android.evgalexandrakaterwth.lostplayer.context.ContextFeatures;
import lab.android.evgalexandrakaterwth.lostplayer.context.MotionFeatures;
import lab.android.evgalexandrakaterwth.lostplayer.context.SimpleLocation;
import lab.android.evgalexandrakaterwth.lostplayer.context.UserTime;
import lab.android.evgalexandrakaterwth.lostplayer.context.Weather;

/**
 * Created by evgenijavstein on 04/07/15.
 * Data is read  into array lists for n seconds this is repeated after i seconds.
 * After n seconds in onDataCompleted data of each sensor is aggregated and stored in contextFeatures,
 * the lists are emptied. They will then be filled again after i seconds. n=duration and i=interval
 * specified in queuePipeline string
 */
public class WriteQueueAction extends Action implements Probe.DataListener {
    //ALL DIFFERENT PROBES WHICH CAN BE HANDLED BY THIS CLASS SO FAR
    public static final String LINEAR_ACCELERATION_SENSOR_PROBE = "edu.mit.media.funf.probe.builtin.LinearAccelerationSensorProbe";
    public static final String GYROSCOPE_SENSOR_PROBE = "edu.mit.media.funf.probe.builtin.GyroscopeSensorProbe";
    public static final String SIMPLE_LOCATION_PROBE = "edu.mit.media.funf.probe.builtin.SimpleLocationProbe";
    public static final String WEATHER_PROBE = "com.lab.android.comsys.rwth.lostapp.funf.WeatherProbe";
    private ContextFeatures contextFeatures;

    ArrayList<Double> xLinearAcc = new ArrayList<Double>();
    ArrayList<Double> yLinearAcc = new ArrayList<Double>();
    ArrayList<Double> zLinearAcc = new ArrayList<Double>();

    ArrayList<Double> xGyros = new ArrayList<Double>();
    ArrayList<Double> yGyros = new ArrayList<Double>();
    ArrayList<Double> zGyros = new ArrayList<Double>();


    ArrayList<SimpleLocation> locations = new ArrayList<SimpleLocation>();

    IJsonObject weather;

    /**
     * Constructor needs <code>ContextFeatures</code> instance
     * whcih will be filled with data, after every interval specified in
     * queuePipeline string.
     *
     * @param contextFeatures
     */
    public WriteQueueAction(ContextFeatures contextFeatures) {


        this.contextFeatures = contextFeatures;
    }


    @Override
    public void onDataReceived(IJsonObject probeConfig, IJsonObject data) {


        final String type = probeConfig.get(RuntimeTypeAdapterFactory.TYPE).getAsString();
        switch (type) {
            case LINEAR_ACCELERATION_SENSOR_PROBE:
                addMotionValues(LINEAR_ACCELERATION_SENSOR_PROBE, data);
                break;
            case GYROSCOPE_SENSOR_PROBE:
                addMotionValues(GYROSCOPE_SENSOR_PROBE, data);
                break;
            case SIMPLE_LOCATION_PROBE:
                addLocationValues(data);
                break;
            case WEATHER_PROBE:
                weather = data;
                break;

        }


        /**
         ensureHandlerExists();
         getHandler().post(new Runnable() {
        @Override public void run() {
        //execute(key, finalData);
        //Log.d(WriteQueueAction.class.getCanonicalName(), "----------onDataReceived: for database: key: "+key+": "+finalData.toString());
        addValues(config, values);
        }
        });
         */

    }

    /**
     * Single location value is put here, list is emtied in <code>onDataCompleted()</code>
     *
     * @param data
     */
    private void addLocationValues(IJsonObject data) {
        SimpleLocation simpleLocation = new SimpleLocation();
        simpleLocation.setmLatitude(data.get("mLatitude").getAsDouble());
        simpleLocation.setmLongitude(data.get("mLongitude").getAsDouble());
        locations.add(simpleLocation);
    }

    /**
     * Last received location is taken here, no accumulation needed so far
     */
    private void extractLastLocation() {
        if(locations.size()>0)
            contextFeatures.setLocation(locations.get(locations.size() - 1));
        locations.clear();
    }

    /**
     * Each single measurement is put here in a list, which will be emptied in <code>onDataCompleted()</code>
     *
     * @param type
     * @param data
     */
    private void addMotionValues(String type, IJsonObject data) {


        double x = data.get("x").getAsDouble();
        double y = data.get("y").getAsDouble();
        double z = data.get("z").getAsDouble();

        if (type.equals(LINEAR_ACCELERATION_SENSOR_PROBE)) {
            xLinearAcc.add(x);
            yLinearAcc.add(y);
            zLinearAcc.add(z);
        }
        if (type.equals(GYROSCOPE_SENSOR_PROBE)) {
            xGyros.add(x);
            yGyros.add(y);
            zGyros.add(z);
        }

    }

    @Override
    public void onDataCompleted(final IJsonObject probeConfig, JsonElement checkpoint) {
        final String type = probeConfig.get(RuntimeTypeAdapterFactory.TYPE).getAsString();
        Date d = new Date();
        switch (type) {

            case LINEAR_ACCELERATION_SENSOR_PROBE:
                extractMotionFeatures(type);
                break;
            case GYROSCOPE_SENSOR_PROBE:
                extractMotionFeatures(type);
                break;
            case SIMPLE_LOCATION_PROBE:
                extractLastLocation();
                Log.d(WriteQueueAction.class.getCanonicalName(), "onDataCompleted: Location gathered: " + d.toString());
                break;
            case WEATHER_PROBE:
                extractWeather();
                Log.d(WriteQueueAction.class.getCanonicalName(), "onDataCompleted: Weather gathered: " + d.toString());
                break;

        }


        /**
         ensureHandlerExists();
         getHandler().post(new Runnable() {
        @Override public void run() {
        //execute(key, finalData);
        //Log.d(WriteQueueAction.class.getCanonicalName(), "----------onDataReceived: for database: key: "+key+": "+finalData.toString());
        // addValues(config, values);
        extractMotionFeatures(probeConfig);
        }
        });

         //setHandler(null); // free system resources as data stream has completed.

         */
    }

    /**
     * Extract subset of weather data available from an API, here api.openweathermap.org
     */
    private void extractWeather() {

        if (weather != null) {
            Log.d(WriteQueueAction.class.getCanonicalName(), "----->" + weather.toString());
            Weather weatherContext = new Weather();
            //weatherContext.setMain(weather.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("main").getAsString());
            weatherContext.setTemp(weather.get("main").getAsJsonObject().get("temp").getAsDouble());
            weatherContext.setHumidity(weather.get("main").getAsJsonObject().get("humidity").getAsDouble());

            if (weather.get("clouds") != null)
                weatherContext.setClouds(weather.get("clouds").getAsJsonObject().get("all").getAsDouble());
            if (weather.get("visibility") != null)
                weatherContext.setVisibility(weather.get("visibility").getAsDouble());
            if (weather.get("rain") != null)
                //workaround because the API can return 1h,2h,3h...rain volumes, we skip the h key and take just the volume
                weatherContext.setRainVolume(Double.parseDouble(weather.get("rain").getAsJsonObject().toString().split(":")[1].replace("}", "")));
            //workaround because the API can return 1h,2h,3h...rain volumes, we skip the h key and take just the volume
            if (weather.get("snow") != null)
                weatherContext.setSnowVolume(Double.parseDouble(weather.get("snow").getAsJsonObject().toString().split(":")[1].replace("}", "")));
            weather = null;
            contextFeatures.setWeather(weatherContext);
        }

    }

    /**
     * Triggers accumulation for different type data: linear acceleration, gyroscope
     * see <code>accumulateMotionFeatures()</code>
     *
     * @param type
     */
    private void extractMotionFeatures(String type) {


        Log.d(LogUtil.TAG, "------------------finished writing probe data " + type);

        MotionFeatures[] linAccFeatures = null;
        MotionFeatures[] gyrosFeatures = null;
        if (type.equals(LINEAR_ACCELERATION_SENSOR_PROBE)
                && (xLinearAcc.size() != 0 && yLinearAcc.size() != 0 && zLinearAcc.size() != 0)) {

            linAccFeatures = new MotionFeatures[3];


            linAccFeatures[0] = accumulateMotionFeatures(xLinearAcc, "xLinearAcc");
            linAccFeatures[1] = accumulateMotionFeatures(yLinearAcc, "yLinearAcc");
            linAccFeatures[2] = accumulateMotionFeatures(zLinearAcc, "zLinearAcc");
            //onModified will be fired

            contextFeatures.setAccelerationFeatures(linAccFeatures);
            xLinearAcc.clear();
            yLinearAcc.clear();
            zLinearAcc.clear();
        }
        if (type.equals(GYROSCOPE_SENSOR_PROBE)
                && (xGyros.size() != 0 && xGyros.size() != 0 && xGyros.size() != 0)) {
            gyrosFeatures = new MotionFeatures[3];

            gyrosFeatures[0] = accumulateMotionFeatures(xGyros, "xGyros");
            gyrosFeatures[1] = accumulateMotionFeatures(yGyros, "yGyros");
            gyrosFeatures[2] = accumulateMotionFeatures(zGyros, "zGyros");
            //onModified will be fired
            contextFeatures.setGyroscopeFeatures(gyrosFeatures);
            UserTime time = new UserTime();//append time
            time.setTime(new Date());
            contextFeatures.setUserTime(time);

            xGyros.clear();
            yGyros.clear();
            zGyros.clear();
        }


    }

    /**
     * Accumulation of data via mean, max, min & sma
     *
     * @param motionData
     * @param relation
     * @return
     */
    private MotionFeatures accumulateMotionFeatures(ArrayList<Double> motionData, String relation) {
        Double[] linAccArray = new Double[motionData.size()];
        motionData.toArray(linAccArray);
        double[] linAccPrimitive = StdStats.toPrimitive(linAccArray);
        //AGGREGATE
        MotionFeatures motionFeatures = new MotionFeatures(relation);
        motionFeatures.setAverage(StdStats.mean(linAccPrimitive));
        motionFeatures.setMax(StdStats.max(linAccPrimitive));
        motionFeatures.setMin(StdStats.min(linAccPrimitive));
        motionFeatures.setStandardDeviation(StdStats.stddev(linAccPrimitive));
        motionFeatures.setSma(StdStats.sum(linAccPrimitive));


        return motionFeatures;
    }

    protected boolean isLongRunningAction() {
        return true;
    }


}
