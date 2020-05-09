package fr.uge.soundroid.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.uge.soundroid.Playlist;
import fr.uge.soundroid.PlaylistAdapter;
import fr.uge.soundroid.R;

public class HomeActivity extends AppCompatActivity {

    private ArrayList<Playlist> playlists, favoris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RecyclerView playlistRV = findViewById(R.id.recyclerPlaylist);
        playlists = Playlist.createPlaylistsList(Color.rgb(45, 45, 107), 15);

        PlaylistAdapter adapter = new PlaylistAdapter(playlists);

        playlistRV.setAdapter(adapter);
        playlistRV.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));

        RecyclerView favorisRV = findViewById(R.id.recyclerFavoris);

        PlaylistAdapter adapterFav = new PlaylistAdapter(playlists);

        favorisRV.setAdapter(adapterFav);
        favorisRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }
}
