package fr.uge.soundroid.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import fr.uge.soundroid.R;
import fr.uge.soundroid.database.entity.Song;
import fr.uge.soundroid.service.PlayerService;

public class PlayerActivity extends AppCompatActivity {
    private PlayerService player;
    private boolean serviceBound = false;
    public static final String Broadcast_PLAY_NEW_AUDIO = "fr.uge.soundroid.activity.PlayNewAudio";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        int songIndex = intent.getIntExtra("MusicIndex", 0);
        List<Song> playlist = (ArrayList<Song>) intent.getSerializableExtra("MusicsList");
        boolean alreadyPlaying = intent.getBooleanExtra("AlreadyPlaying", false);
        Song song = playlist.get(songIndex);
        String playlistName = intent.getStringExtra("PlaylistName");
        ((TextView) findViewById(R.id.musicName)).setText(song.getMusicName());
        ((TextView) findViewById(R.id.musicArtist)).setText(song.getArtist());
        ((TextView) findViewById(R.id.playlistNameIcon)).setText(playlistName);
        ((ImageView) findViewById(R.id.likeButton))
                .setImageBitmap(song.getBitmapLike(getApplicationContext()));
        Log.d("PlayerActivity", playlist.toString());
        if (!alreadyPlaying) {
            playAudio(songIndex, playlist);
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
