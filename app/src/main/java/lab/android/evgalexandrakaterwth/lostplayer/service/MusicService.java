package lab.android.evgalexandrakaterwth.lostplayer.service;

import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import lab.android.evgalexandrakaterwth.lostplayer.LOSTPlayerActivity;
import lab.android.evgalexandrakaterwth.lostplayer.R;
import lab.android.evgalexandrakaterwth.lostplayer.adapter.SongListAdapter;
import lab.android.evgalexandrakaterwth.lostplayer.context.ContextFeatures;
import lab.android.evgalexandrakaterwth.lostplayer.model.SongItem;
import lab.android.evgalexandrakaterwth.lostplayer.requestAPI.ApiPathEnum;
import lab.android.evgalexandrakaterwth.lostplayer.requestAPI.GetListOfSongsTask;
import lab.android.evgalexandrakaterwth.lostplayer.requestAPI.LearnPostRequest;
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
        boolean doRecommend = pref.getBoolean(LOSTPlayerActivity.IS_CHECKED_KEY, false);
        if (doRecommend) {
            LearnPostRequest task = new LearnPostRequest(getApplicationContext(), ApiPathEnum.GET_RECOMMENDED.getPath());
            task.setOnResponseListener(
                    new OnResponseListener<Boolean>() {
                        @Override
                        public void onResponse(Boolean success) {
                            Log.i(TAG, "Success");
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, errorMessage);
                        }
                    }
            );
            task.send(new ContextFeatures(), position, null, true);
//            GetListOfSongsTask task = new GetListOfSongsTask(ApiPathEnum.GET_RECOMMENDED);
//            task.setOnResponseListener(new OnResponseListener<List<SongItem>>() {
//                @Override
//                public void onResponse(List<SongItem> songsList) {
//                    if (songsList != null && songsList.size() > 0) {
//                        SongItem firstClosestSong = songsList.get(0);
//                        setSong((int) firstClosestSong.getID());
//                        playSong();
//                    }
//                }
//
//                @Override
//                public void onError(String errorMessage) {
//                    Log.e(TAG, errorMessage);
//                }
//            });
//            task.send();
        } else {
            if (this.position > 0) {
                mediaPlayer.reset();
                playNextTrack();
            }
        }
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
