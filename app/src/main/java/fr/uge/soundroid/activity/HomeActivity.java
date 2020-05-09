package fr.uge.soundroid.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    private RecyclerView playlistRV, favorisRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        playlists = Playlist.createPlaylistsList(15, "playlist_icon.png", "Playlist");
        favoris = Playlist.createFavorisList();

        playlistRV = findViewById(R.id.recyclerPlaylist);
        PlaylistAdapter adapter = new PlaylistAdapter(playlists);
        playlistRV.setAdapter(adapter);
        updateLayoutManager(playlistRV);

        favorisRV = findViewById(R.id.recyclerFavoris);
        PlaylistAdapter adapterFav = new PlaylistAdapter(favoris);
        favorisRV.setAdapter(adapterFav);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        llm.scrollToPositionWithOffset(0, -1);
        favorisRV.setLayoutManager(llm);
        adapterFav.setOnItemClickListener(new PlaylistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Playlist p = favoris.get(position);
                playlists = Playlist.createPlaylistsList(15, p.getPathIcon(), p.getName());
                TextView tv = findViewById(R.id.title);
                tv.setText(p.getName());
                playlistRV.setAdapter(new PlaylistAdapter(playlists));
            }
        });

    }

    private void updateLayoutManager(RecyclerView rv) {
        rv.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
    }
}
