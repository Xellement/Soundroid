package fr.uge.soundroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.uge.soundroid.R;
import fr.uge.soundroid.SongAdapter;
import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.Song;
import fr.uge.soundroid.database.viewmodel.PlaylistSongsJoinViewModel;

public class PlaylistActivity extends AppCompatActivity {

    private RecyclerView rv;
    private PlaylistSongsJoinViewModel playlistSongsJoinViewModel;
    private SongAdapter adapter = new SongAdapter(new ArrayList<Song>());
    private Playlist playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        playlistSongsJoinViewModel = ViewModelProviders.of(this).get(PlaylistSongsJoinViewModel.class);
        playlist = (Playlist) getIntent().getSerializableExtra("Playlist");
        String iconPath = playlist.getPathIcon();
        iconPath = iconPath.substring(0, iconPath.indexOf("."));
        int iconId = getResources().getIdentifier(iconPath, "drawable", getPackageName());

        playlistSongsJoinViewModel.getSongsFromPlaylist(playlist.getPlaylistId()).observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                adapter.setMusicList(songs);
            }
        });

        TextView tv = findViewById(R.id.playlistName);
        tv.setText(playlist.getName());
        tv = findViewById(R.id.playlistArtist);
        tv.setText(playlist.getPlaylistArtist());
        ImageView iv = findViewById(R.id.imageView);
        iv.setImageResource(iconId);
        rv = findViewById(R.id.recyclerPlaylist);
        // TODO : maybe find a way to refactor this because its the same code as in HomeActivity.setMusicClickListener()
        adapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext() , PlayerActivity.class);
                intent.putExtra("MusicIndex", position);
                intent.putExtra("MusicsList", (ArrayList<Song>) adapter.getMusics());
                intent.putExtra("PlaylistName", playlist.getName());
                startActivity(intent);
            }
        });
        rv.setAdapter(adapter);
        updateLayoutManager(rv, 1);
    }

    private void updateLayoutManager(RecyclerView rv, int span) {
        rv.setLayoutManager(new GridLayoutManager(this, span, GridLayoutManager.VERTICAL, false));
    }

}
