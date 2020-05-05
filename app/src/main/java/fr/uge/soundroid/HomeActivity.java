package fr.uge.soundroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ArrayList<Playlist> playlists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RecyclerView playlistRV = findViewById(R.id.recyclerPlaylist);
        playlists = Playlist.createPlaylistsList();

        PlaylistAdapter adapter = new PlaylistAdapter(playlists);

        playlistRV.setAdapter(adapter);
        playlistRV.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));

    }
}
