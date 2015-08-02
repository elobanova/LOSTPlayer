package lab.android.evgalexandrakaterwth.lostplayer.funf;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.google.gson.JsonElement;

import java.util.List;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.config.ConfigUpdater;
import edu.mit.media.funf.config.Configurable;
import edu.mit.media.funf.datasource.StartableDataSource;
import edu.mit.media.funf.pipeline.Pipeline;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.storage.FileArchive;
import edu.mit.media.funf.storage.RemoteFileArchive;
import lab.android.evgalexandrakaterwth.lostplayer.context.ContextFeatures;

/**
 * QueuePipeline uses WriteQueueAction instead of standard funf actions.
 * WriteQueueAction stores the measured data temporary discarding it,
 * after accumulation into the <code>ContextFeatures</code> instance.
 * An <code>ContextFeatures</code> instance can be obtained directly via
 * <code>getContextFeatures()</code> or via <code>OnContextChangedListener</code> callback.
 * The latter provides always at least motion features and user time.<code>getContextFeatures()</code>
 * might return null if no features were accumulated yet.Make sure to include the following permissions/features
 * in  AndroidManifest.xml

 * Created by evgenijavstein on 04/07/15.
 */


public class QueuePipeline implements Pipeline {

    public static final int HARVEST_INTERVAL = 120000;

    public interface OnContextChangedListener {
        void onChanged(ContextFeatures contextFeatures);
    }


    //clean contextFeatures which will be updated all the time from different probes
    private ContextFeatures contextFeatures = new ContextFeatures();
    //this is to be provided by activity, if notification of this kind is needed
    //context changes of 2 in a harvested by this callback, see below
    private OnContextChangedListener onContextChangedListener;


    @Configurable
    protected String name = "actiongraph";

    @Configurable
    protected int version = 1;

    @Configurable
    protected FileArchive archive = null;

    @Configurable
    protected RemoteFileArchive upload = null;

    @Configurable
    protected ConfigUpdater update = null;

    @Configurable
    protected List<StartableDataSource> data = null;


    private boolean enabled;
    private FunfManager manager;

    private Looper looper;
    private Handler handler;
    private WriteQueueAction writeQueueAction;
    private static QueuePipeline instance;

    public static QueuePipeline getInstance(){
        return instance;
    }

    @Override
    public void onCreate(FunfManager funfManager) {
        this.manager = funfManager;
        instance=this;

        HandlerThread thread = new HandlerThread(getClass().getName());
        thread.start();
        this.looper = thread.getLooper();
        this.handler = new Handler(looper);

        writeQueueAction = new WriteQueueAction(this.contextFeatures);
        writeQueueAction.setHandler(handler);

        //run all probes, in between (every 2 min)gather all data
        handler.post(new Runnable() {
            @Override
            public void run() {
                setupDataSources();

                //notify changes of context features every 2 min
                try {

                    while (true) {

                        Thread.sleep(HARVEST_INTERVAL);
                        changeContext();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Method delivers context to activity if corresponding listener implementation was provided and
     * the activity existing
     */
    private void changeContext() {
        if (onContextChangedListener != null) {//maybe null if detached

            onContextChangedListener.onChanged(contextFeatures);
        }
    }

    @Override
    public void onRun(String s, JsonElement jsonElement) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    /**
     * Each <code>StartableDataSource </code> represents here the
     * different probes specified in queuePipeline string. Each probe
     * runs on it's own thread.
     */
    protected void setupDataSources() {
        if (enabled == false) {
            //all datasources (probe) which are listed in configuration JSON of pipeline
            //are iterated and assigned with a listener which writes the receiving data to db queue
            for (StartableDataSource dataSource : data) {
                dataSource.setListener((Probe.DataListener) writeQueueAction);
            }

            for (StartableDataSource dataSource : data) {
                dataSource.start();
            }

            enabled = true;
        }
    }

    /**
     * Returns the <code>ContextFeatures</code> instance, might be null
     * if no accumulation was done yet.
     * @return
     */
    public ContextFeatures getContextFeatures() {
        return contextFeatures;
    }

    /**
     * Fires every 2 min delivering at least motion information (acceleration and gyroscope) and
     * <code>UserTime</code>
     * Additinaly there is the <code>Weather</code> and <code>Location</code>. Usually the weather
     * information is available in 2. or 3. callback.
     * @param onContextChangedListener
     */
    public void setOnContextChangedListener(OnContextChangedListener onContextChangedListener) {
        this.onContextChangedListener = onContextChangedListener;
    }
}
