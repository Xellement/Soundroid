package fr.uge.soundroid.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
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
    private boolean playing;
    private Intent intent;
    private Song song;
    private int songIndex;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        songIndex = intent.getIntExtra("MusicIndex", 0);
        final List<Song> playlist = (ArrayList<Song>) intent.getSerializableExtra("MusicsList");
        boolean alreadyPlaying = intent.getBooleanExtra("AlreadyPlaying", false);
        final String playlistName = intent.getStringExtra("PlaylistName");
        seekBar = findViewById(R.id.musicProgress);
        song = refreshActivity(playlist, songIndex, playlistName);
        ((ImageView) findViewById(R.id.likeButton))
                .setImageBitmap(song.getBitmapLike(getApplicationContext()));
        Log.d("PlayerActivity", playlist.toString());

        findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songIndex == playlist.size() - 1) {
                    songIndex = 0;
                } else {
                    songIndex++;
                }
                player.skipToNext();
                refreshActivity(playlist, songIndex, playlistName);
            }
        });

        findViewById(R.id.prevButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songIndex == 0) {
                    player.seekTo(0);
                } else {
                    songIndex--;
                    player.skipToPrevious();
                }
                refreshActivity(playlist, songIndex, playlistName);
            }
        });

        final ImageView playPauseButton = findViewById(R.id.playButton);
        playPauseButton.setImageResource(R.drawable.pause);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
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
        });

        seekBar = findViewById(R.id.musicProgress);
        seekBar.setMax((int) (song.getSongDuration()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                player.seekTo(songProgress);
            }
        });

        new Thread() {
            @Override
            public void run() {
                songPosition = 0;
                while (songPosition < song.getSongDuration()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (playing) {
                        songPosition += 1000;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.actualTimer)).setText(convertMsToMinutes(songPosition));
                            seekBar.setProgress(songPosition);
                        }
                    });
                }
            }
        }.start();

        if (!alreadyPlaying) {
            playAudio(songIndex, playlist);
        }
        playing = true;
    }

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

    private Song refreshActivity(List<Song> playlist, int songIndex, String playlistName) {
        Song song = playlist.get(songIndex);
        ((TextView) findViewById(R.id.musicName)).setText(song.getMusicName());
        ((TextView) findViewById(R.id.musicArtist)).setText(song.getArtist());
        ((TextView) findViewById(R.id.playlistNameIcon)).setText(playlistName);

        ((TextView) findViewById(R.id.endTimer)).setText(convertMsToMinutes((int) song.getSongDuration()));
        ((ImageView) findViewById(R.id.likeButton)).setImageBitmap(song.getBitmapLike(getApplicationContext()));
        seekBar.setProgress(0);
        songPosition = 0;
        return song;
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
}
