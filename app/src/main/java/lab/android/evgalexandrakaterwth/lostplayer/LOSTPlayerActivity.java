package lab.android.evgalexandrakaterwth.lostplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController;

import lab.android.evgalexandrakaterwth.lostplayer.adapter.SongListAdapter;
import lab.android.evgalexandrakaterwth.lostplayer.context.ContextFeatures;
import lab.android.evgalexandrakaterwth.lostplayer.context.FunfContextClient;
import lab.android.evgalexandrakaterwth.lostplayer.funf.QueuePipeline;
import lab.android.evgalexandrakaterwth.lostplayer.model.SongItem;
import lab.android.evgalexandrakaterwth.lostplayer.requestAPI.ApiPathEnum;
import lab.android.evgalexandrakaterwth.lostplayer.requestAPI.GetListOfSongsTask;
import lab.android.evgalexandrakaterwth.lostplayer.requestAPI.OnResponseListener;
import lab.android.evgalexandrakaterwth.lostplayer.service.MusicController;
import lab.android.evgalexandrakaterwth.lostplayer.service.MusicService;

/**
 * Created by ekaterina on 26.07.2015.
 */
public class LOSTPlayerActivity extends Activity implements MediaController.MediaPlayerControl {
    private static final String TAG = "LOST Player Activity";
    public static final String IS_CHECKED_KEY = "isCheckedKey";

    public static final int NO_RECOMMEND_ON_CONTEXT = 0;
    public static final int RECOMMEND_ON_CONTEXT = 1;
    public static final int RECOMMEND_ON_RUNNING_CONTEXT = 2;
    public static final int RECOMMEND_ON_LEARNING_CONTEXT = 3;

    public static final String MY_PREFERENCES = "MyPrefs";
    private static SharedPreferences sharedpreferences;

    private List<SongItem> listOfSongs;
    private ListView trackListView;

    private MusicService service;
    private Intent playIntent;
    private boolean isBound = false;

    private MusicController controller;
    private boolean isPaused = false;
    private boolean isPlaybackPaused = false;

    private int checked;
    private static FunfContextClient funfContextClient;
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            LOSTPlayerActivity.this.service = binder.getService();
            LOSTPlayerActivity.this.service.setList(listOfSongs);
            LOSTPlayerActivity.this.service.setController(controller);
            isBound = true;

            /* modified*/
            getSongList();
            Collections.sort(listOfSongs, new Comparator<SongItem>() {
                public int compare(SongItem song, SongItem other) {
                    return song.getTitle().compareTo(other.getTitle());
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_player_lost);

        funfContextClient = new FunfContextClient(this);
        //print context change, can be removed, or used if notification about change is needed
        funfContextClient.setOnContextChangedListener(new QueuePipeline.OnContextChangedListener() {
            @Override
            public void onChanged(ContextFeatures contextFeatures) {
                Date date = new Date();
                String context = contextFeatures.getAsJSON().toString();
                String contextChange = "Context changed: " + date.toString() + " " + context;
                Log.d(TAG, contextChange);
            }
        });

        this.trackListView = (ListView) findViewById(R.id.song_list);
        this.listOfSongs = new ArrayList<>();

        /*modified*/
        //removed getSongList(); etc.
        setController();
        //2.CONNECT IN ON_CREATE
        funfContextClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (this.playIntent == null) {
            this.playIntent = new Intent(getApplicationContext(), MusicService.class);
            bindService(this.playIntent, this.musicConnection, Context.BIND_AUTO_CREATE);
            startService(this.playIntent);
        }
    }

    public void onSongSelectedFromList(View view) {
        this.service.setSong(Integer.parseInt(view.getTag().toString()));
        this.service.playSong();
        if (this.isPlaybackPaused) {
            setController();
            this.isPlaybackPaused = false;
        }
        this.controller.show(0);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable;
        if (sharedpreferences != null)


                this.checked = sharedpreferences.getInt(IS_CHECKED_KEY, NO_RECOMMEND_ON_CONTEXT);
                switch (checked) {
                    case NO_RECOMMEND_ON_CONTEXT:
                        checkable = menu.findItem(R.id.checkable_norecommend_menu);
                        checkable.setChecked(true);
                        break;
                    case RECOMMEND_ON_CONTEXT:
                        checkable = menu.findItem(R.id.checkable_recommend_menu);
                        checkable.setChecked(true);
                        break;
                    case RECOMMEND_ON_RUNNING_CONTEXT:
                        checkable = menu.findItem(R.id.checkable_recommend_running_menu);
                        checkable.setChecked(true);
                        break;
                    case RECOMMEND_ON_LEARNING_CONTEXT:
                        checkable = menu.findItem(R.id.checkable_recommend_learning_menu);
                        checkable.setChecked(true);
                        break;
                }



        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        switch (item.getItemId()) {
            case R.id.checkable_norecommend_menu:
                item.setChecked(true);

                editor.putInt(IS_CHECKED_KEY, NO_RECOMMEND_ON_CONTEXT);
                editor.commit();
                return true;
            case R.id.checkable_recommend_menu:

                item.setChecked(true);

                editor.putInt(IS_CHECKED_KEY, RECOMMEND_ON_CONTEXT);
                editor.commit();
                return true;
            case R.id.checkable_recommend_running_menu:

                item.setChecked(true);

                editor.putInt(IS_CHECKED_KEY, RECOMMEND_ON_RUNNING_CONTEXT);
                editor.commit();
                return true;
            case R.id.checkable_recommend_learning_menu:

                item.setChecked(true);

                editor.putInt(IS_CHECKED_KEY, RECOMMEND_ON_LEARNING_CONTEXT);
                editor.commit();
                return true;

            case R.id.action_end:
                stopService(this.playIntent);
                this.service = null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getSongList() {
        GetListOfSongsTask getListOfSongsTask = new GetListOfSongsTask(ApiPathEnum.ALL_SONGS);
        getListOfSongsTask.setOnResponseListener(new OnResponseListener<List<SongItem>>() {

            @Override
            public void onResponse(List<SongItem> songsList) {
                LOSTPlayerActivity.this.listOfSongs = songsList;
                SongListAdapter adapter = new SongListAdapter(LOSTPlayerActivity.this, LOSTPlayerActivity.this.listOfSongs);
                trackListView.setAdapter(adapter);
                LOSTPlayerActivity.this.service.setList(LOSTPlayerActivity.this.listOfSongs);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, errorMessage);
            }
        });
        getListOfSongsTask.send();
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (this.service != null && this.isBound && this.service.isPlaying()) {
            return this.service.getPosition();
        } else return 0;
    }

    @Override
    public int getDuration() {
        if (this.service != null && this.isBound && this.service.isPlaying()) {
            return this.service.getDuration();
        } else return 0;
    }

    @Override
    public boolean isPlaying() {
        if (this.service != null && this.isBound)
            return this.service.isPlaying();
        return false;
    }

    @Override
    public void pause() {
        this.isPlaybackPaused = true;
        this.service.pausePlayer();
    }

    @Override
    public void seekTo(int position) {
        this.service.seek(position);
    }

    @Override
    public void start() {
        this.service.startService();
    }

    private void setController() {
        this.controller = new MusicController(this);
        this.controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousTrack();
            }
        });
        this.controller.setMediaPlayer(this);
        this.controller.setAnchorView(findViewById(R.id.song_list));
        this.controller.setEnabled(true);
    }

    private void playNext() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(LOSTPlayerActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        int recommendOpt = pref.getInt(LOSTPlayerActivity.IS_CHECKED_KEY, NO_RECOMMEND_ON_CONTEXT);
        if (recommendOpt!=0) {
            this.service.playRecommended(recommendOpt);
        } else {
            this.service.playNextTrack();
        }
        if (this.isPlaybackPaused) {
            setController();
            this.isPlaybackPaused = false;
        }
        //learn anyways, false because skipped
        this.service.storePlayed();
        this.service.sendLearningData(false,recommendOpt);
        this.controller.show(0);
    }

    private void playPreviousTrack() {
        this.service.playPreviousTrack();
        if (this.isPlaybackPaused) {
            setController();
            this.isPlaybackPaused = false;
        }
        this.controller.show(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.isPaused) {
            setController();
            this.isPaused = false;
        }
    }

    @Override
    protected void onStop() {
        this.controller.hide();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //3. DISCONNECT IN ON_DESTROY
        funfContextClient.disconnect();
        stopService(playIntent);
        this.service = null;
        super.onDestroy();
    }

    public static FunfContextClient getFunfContextClient() {
        return funfContextClient;
    }
}