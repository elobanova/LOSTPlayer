package lab.android.evgalexandrakaterwth.lostplayer.context;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import lab.android.evgalexandrakaterwth.lostplayer.funf.QueuePipeline.OnContextChangedListener;
import com.google.gson.Gson;


import edu.mit.media.funf.FunfManager;
import lab.android.evgalexandrakaterwth.lostplayer.funf.QueuePipeline;

/**
 * Created by evgenijavstein on 12/07/15.
 * Simple client to receive context information using FunfManager service from funf framework and the corresponding probes.
 * The delivered ContextFeatures currently only contains the following features:
 *
 * <code>
 *     <ul>
 *         <li>Acceleration: max, min, mean, sma, stddev</li>
 *          <li>Gyroscope: max, min, mean, sma, stddev</li>
 *           <li>Weather: main, temp, rainVolume, snowVolume, visibility, humidity, clouds</li>
 *            <li>Simple location: latitude, longitude (power saving mode)</li>
 *     </ul>
 *
 *
 * </code>
 *
 * Include the these permissions and features in AndroidManifest.xml:
 *<code>
 *     <ul>
 *         <li>android.hardware.sensor.accelerometer</li>
 *         <li>android.permission.BATTERY_STATS</li>
 *         <li>android.permission.WAKE_LOCK</li>
 *         <li>android.permission.WRITE_EXTERNAL_STORAGE</li>
 *         <li>android.permission.ACCESS_COARSE_LOCATION</li>
 *         <li>android.permission.ACCESS_FINE_LOCATION</li>
 *         <li>android.permission.INTERNET</li>
 *
 *         <li>android.hardware.sensor.accelerometer</li>
 *         <li>android.hardware.sensor.gyroscope</li>
 *         <li>android.hardware.location</li>
 *     </ul>

 *</code>
 *
 */
public class FunfContextClient {
    private static final String PIPELINE_NAME = "queuePipeline";
    private FunfManager funfManager;
    private QueuePipeline pipeline;
    private Activity activity;

    private OnContextChangedListener onContextChangedListener;
    private ServiceConnection funfManagerConn=funfManagerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            funfManager = ((FunfManager.LocalBinder) service).getManager();

            final Gson gson = funfManager.getGson();

            pipeline = (QueuePipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);

            pipeline.setOnContextChangedListener(new OnContextChangedListener() {
                @Override
                public void onChanged(final ContextFeatures contextFeatures) {

                    //Implement context change check in isContextChange()
                    if(isContextChange(contextFeatures))
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FunfContextClient.this.onContextChangedListener.onChanged(contextFeatures);
                            }
                        });
                }
            });
            funfManager.enablePipeline(PIPELINE_NAME);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            funfManager = null;
        }

    };

    public FunfContextClient(Activity activity){
        this.activity=activity;
    }

    /**
     * Connect to service, please use only in <code>Activity.onCreate()</code>
     */
    public void connect(){

        // Bind to the service, to create the connection with FunfManager
        activity.bindService(new Intent(activity, FunfManager.class), funfManagerConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * Make sure to call it in <code>Activity.onDestroy()
     */
    public void disconnect(){
        activity.unbindService(funfManagerConn);
    }

    /**
     * Might return null initially, use <code>setOnContextChangedListener</code>
     * to be notified.
     * @return
     */
    public ContextFeatures getCurrentContext(){
        return pipeline.getContextFeatures();

    }

    /**
     * Context update every 2 min. Call before first call of <code>connect()</code>.
     * Note: the framework decides, when to start the probes, although you configure duration and
     * interval, the actual start may be delayed.(The offset in the config file can be
     * used to set a start up time static, but this is not what is needed to track
     * user context continuously).The following  happens in most cases for weather
     * and simple location probe: the start of the probes is delayed, but the scedule is kept
     * as configured in <code>queuePipeline</code> configuration string.
     * But after some time (max time=<code>interval</code>) you receive the whole context information.
     *
     * <b>Note: it might happen that no location is available: location and weather are of course null in that case</b>
     *
     * @param onContextChangedListener
     */
    public void  setOnContextChangedListener(OnContextChangedListener onContextChangedListener){
        this.onContextChangedListener=onContextChangedListener;
    }

    /**
     * Check if context change occured using tresholds if needed
     * @param contextFeatures
     * @return
     */
    private boolean isContextChange(ContextFeatures contextFeatures){

        //IMPLEMENT HERE A CHECK IF REALLY A CONTEXT CHANGED AND NOT JUST THE FEATURES
        return true;
    }

}
