package lab.android.evgalexandrakaterwth.lostplayer.service;

import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import org.json.JSONObject;
import lab.android.evgalexandrakaterwth.lostplayer.LOSTPlayerActivity;
import lab.android.evgalexandrakaterwth.lostplayer.R;
import lab.android.evgalexandrakaterwth.lostplayer.context.FunfContextClient;
import lab.android.evgalexandrakaterwth.lostplayer.model.SongItem;
import lab.android.evgalexandrakaterwth.lostplayer.requestAPI.ApiPathEnum;
import lab.android.evgalexandrakaterwth.lostplayer.requestAPI.LearnPostRequest;
import lab.android.evgalexandrakaterwth.lostplayer.requestAPI.RecommendationPostRequest;
import lab.android.evgalexandrakaterwth.lostplayer.requestAPI.OnResponseListener;

/**
 * Created by ekaterina on 26.07.2015.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private static final int NOTIFY_ID = 1;
    public static final String TAG = "MUSIC SERVICE";

    private MediaPlayer player;
    private List<SongItem> listOfSongs;
    private int position;
    private String title = "";
    private final IBinder musicBinder = new MusicBinder();
    private MusicController controller;

    public void onCreate() {
        super.onCreate();
        this.position = 0;
        this.player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(List<SongItem> listOfSongs) {
        this.listOfSongs = listOfSongs;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        this.player.stop();
        this.player.release();
        return false;
    }

    public void playSong() {
        this.player.reset();
        SongItem songToBePlayed = this.listOfSongs.get(position);
        this.title = songToBePlayed.getTitle();
        long currentSong = songToBePlayed.getID();
        Uri uri = Uri.parse(ApiPathEnum.PLAY.getPath() + currentSong);
        try {
            this.player.setDataSource(getApplicationContext(), uri);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        this.player.prepareAsync();
    }

    public void setSong(int songID) {
        this.position = songID;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(LOSTPlayerActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        int recommendOpt = pref.getInt(LOSTPlayerActivity.IS_CHECKED_KEY, LOSTPlayerActivity.NO_RECOMMEND_ON_CONTEXT);
        if (recommendOpt != LOSTPlayerActivity.NO_RECOMMEND_ON_CONTEXT) {
            playRecommended(recommendOpt);
        } else {
            if (this.position >= 0) {
                mediaPlayer.reset();
                playNextTrack();
            }
        }
        //anyways send learning data

        sendLearningData(true, recommendOpt);
    }

    public void sendLearningData(boolean isPositive, int recommendOpt) {
        LearnPostRequest request = new LearnPostRequest(getApplicationContext(), ApiPathEnum.LEARN.getPath());
        request.setOnResponseListener(new OnResponseListener<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                Log.i(TAG, "Result of learn send: " + response);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, errorMessage);
            }
        });
        JSONObject userContext = LOSTPlayerActivity.getFunfContextClient().getCurrentContext(recommendOpt);//learn on selected context
        if (userContext != null) {
            request.send(userContext, this.position, null, isPositive);
        }
    }

    public void playRecommended(int recommendOpt) {
        RecommendationPostRequest task = new RecommendationPostRequest(getApplicationContext(), ApiPathEnum.GET_RECOMMENDED.getPath());
        task.setOnResponseListener(
                new OnResponseListener<List<SongItem>>() {
                    @Override
                    public void onResponse(List<SongItem> songItemList) {
                        Log.i(TAG, "Success");
                        if (songItemList != null && songItemList.size() > 0) {
                            SongItem firstClosestSong = songItemList.get(0);
                            setSong((int) firstClosestSong.getID());
                            playSong();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, errorMessage);
                    }
                }
        );
        JSONObject userContext = LOSTPlayerActivity.getFunfContextClient().getCurrentContext(recommendOpt);
        if (userContext != null)
            task.send(userContext, null);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        Log.e(TAG, "Playback Error");
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Intent notIntent = new Intent(this, LOSTPlayerActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play_icon)
                .setTicker(title)
                .setOngoing(true)
                .setContentTitle("LOST Player")
                .setContentText(title);
        Notification notification = builder.build();
        startForeground(NOTIFY_ID, notification);
        if (this.controller != null) {
            controller.show(0);
        }
    }

    public int getPosition() {
        return this.player.getCurrentPosition();
    }

    public int getDuration() {
        return this.player.getDuration();
    }

    public boolean isPlaying() {
        return this.player.isPlaying();
    }

    public void pausePlayer() {
        this.player.pause();
    }

    public void seek(int position) {
        this.player.seekTo(position);
    }

    public void startService() {
        this.player.start();
    }

    public void playPreviousTrack() {
        this.position--;
        if (this.position < 0) {
            this.position = this.listOfSongs.size() - 1;
        }
        playSong();
    }

    public void playNextTrack() {
        this.position++;
        if (this.position >= this.listOfSongs.size()) {
            this.position = 0;
        }
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void setController(MusicController controller) {
        this.controller = controller;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}