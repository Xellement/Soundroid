package fr.uge.soundroid.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.uge.soundroid.R;
import fr.uge.soundroid.database.entity.Song;
import fr.uge.soundroid.service.PlayerService;

public class PlayerActivity extends AppCompatActivity {
    private PlayerService player;
    private boolean serviceBound = false;
    private int songPosition;
    public static final String Broadcast_PLAY_NEW_AUDIO = "fr.uge.soundroid.activity.PlayNewAudio";
    private Song song;
    private int songIndex;
    private boolean playing;
    private SeekBar seekBar;
    private Handler handler;
    private String playlistName;
    private List<Song> playlist;
    private ImageView playPauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        handler = new Handler();

        Intent intent = getIntent();
        songIndex = intent.getIntExtra("MusicIndex", 0);
        playlist = (ArrayList<Song>) intent.getSerializableExtra("MusicsList");
        boolean alreadyPlaying = intent.getBooleanExtra("AlreadyPlaying", false);
        playlistName = intent.getStringExtra("PlaylistName");
        seekBar = findViewById(R.id.musicProgress);
        song = refreshActivity(playlist.get(songIndex), playlist, 0);
        Log.d("PlayerActivity", playlist.toString());

        findViewById(R.id.nextButton).setOnClickListener(onClickNext());
        findViewById(R.id.prevButton).setOnClickListener(onClickPrevious());
        playPauseButton = findViewById(R.id.playButton);
        playPauseButton.setImageResource(R.drawable.pause);
        playPauseButton.setOnClickListener(onClickPlayPause());

        seekBar = findViewById(R.id.musicProgress);
        seekBar.setMax((int) (song.getSongDuration()));
        seekBar.setOnSeekBarChangeListener(onSeekBarChanged());

        if (!alreadyPlaying) {
            playAudio(songIndex, playlist);
        }
        playing = true;
    }

    private Runnable increaseTimer = new Runnable() {
        @Override
        public void run() {
            if (songPosition >= song.getSongDuration()) {
                player.skipToNext();
                refreshActivity(player.getCurrentSong(), player.getPlaylist(), player.getPosition());
            }
            else if (playing) {
                songPosition += 1000;
            }
            handler.postDelayed(getAndIncreaseTimer(), 1000);
        }
    };

    private Runnable getAndIncreaseTimer() {
        Song tmpSong;
        List<Song> tmpPlaylist;
        int currentPos;
        if (player != null) {
            tmpSong = player.getCurrentSong();
            tmpPlaylist = player.getPlaylist();
            currentPos = player.getPosition();
        }
        else {
            tmpSong = song;
            tmpPlaylist = playlist;
            currentPos = songPosition;
        }
        refreshActivity(tmpSong, tmpPlaylist, currentPos);
        return increaseTimer;
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChanged() {
        return new SeekBar.OnSeekBarChangeListener() {
            int songProgress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                songProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // noop
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                songPosition = songProgress;
                ((TextView) findViewById(R.id.actualTimer)).setText(convertMsToMinutes(songPosition));
                seekBar.setProgress(songPosition);
                player.seekTo(songProgress);
            }
        };
    }
    private View.OnClickListener onClickPlayPause() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    player.pauseSong();
                    playPauseButton.setImageResource(R.drawable.play);
                    playing = false;
                } else {
                    player.resumeSong();
                    playPauseButton.setImageResource(R.drawable.pause);
                    playing = true;
                }
            }
        };
    }

    private View.OnClickListener onClickNext() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.skipToNext();
                playing = true;
                refreshActivity(player.getCurrentSong(), player.getPlaylist(), player.getPosition());
            }
        };
    }

    private View.OnClickListener onClickPrevious() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songIndex == 0) {
                    player.seekTo(0);
                    refreshActivity(player.getCurrentSong(), player.getPlaylist(), player.getPosition());
                } else {
                    player.skipToPrevious();
                    playing = true;
                    refreshActivity(player.getCurrentSong(), player.getPlaylist(), player.getPosition());
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        increaseTimer.run();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(increaseTimer);
    }

    private void updateSeekBar() {
        ((TextView) findViewById(R.id.actualTimer)).setText(convertMsToMinutes(songPosition));
        seekBar.setProgress(songPosition);
    }

    private Song refreshActivity(Song currentSong, List<Song> currentPlaylist, int currentPosition) {
        if (! currentSong.equals(song)) {
            song = currentSong;
            playlist = currentPlaylist;
            ((TextView) findViewById(R.id.musicName)).setText(song.getMusicName());
            ((TextView) findViewById(R.id.musicArtist)).setText(song.getArtist());
            ((TextView) findViewById(R.id.playlistNameIcon)).setText(playlistName);
            ((ImageView) findViewById(R.id.likeButton))
                    .setImageBitmap(song.getBitmapLike(getApplicationContext()));
            ((TextView) findViewById(R.id.endTimer)).setText(convertMsToMinutes((int) song.getSongDuration()));
            ((ImageView) findViewById(R.id.likeButton)).setImageBitmap(song.getBitmapLike(getApplicationContext()));
        }
        Log.d("REFRESH", "Playing is " + playing);
        if (playing) {
            playPauseButton.setImageResource(R.drawable.pause);
        }
        songPosition = currentPosition;
        updateSeekBar();
        seekBar.setMax((int) (song.getSongDuration()));
        return song;
    }

    private void playAudio(int songIndex, List<Song> playlist) {
        //Check if service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, PlayerService.class);
            playerIntent.putExtra("SongIndex", songIndex);
            playerIntent.putExtra("Playlist", (ArrayList<Song>) playlist);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            broadcastIntent.putExtra("SongIndex", songIndex);
            broadcastIntent.putExtra("Playlist", (ArrayList<Song>) playlist);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Destroy", "Destroying activity");
        if (serviceBound) {
            Log.d("destroy", "Should unbind service");
            unbindService(serviceConnection);
        }
    }

    //Binding this Client to the MediaPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(PlayerActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
            Toast.makeText(PlayerActivity.this, "Service Unbound", Toast.LENGTH_SHORT).show();
        }
    };

    @SuppressLint("DefaultLocale")
    private String convertMsToMinutes(int ms) {
        if (ms > 3600000) {
            return String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(ms),
                    TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                    TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
        }
        long minutes = (ms / 1000) / 60;
        long seconds = (ms / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
