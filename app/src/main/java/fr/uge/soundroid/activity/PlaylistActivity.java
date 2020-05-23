package fr.uge.soundroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import fr.uge.soundroid.database.entity.Song;
import fr.uge.soundroid.database.viewmodel.PlaylistSongsJoinViewModel;

public class PlaylistActivity extends AppCompatActivity {

    private ArrayList<Song> musics;
    private RecyclerView rv;
    private PlaylistSongsJoinViewModel playlistSongsJoinViewModel;
    private SongAdapter adapter = new SongAdapter(new ArrayList<Song>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        long id = getIntent().getLongExtra("PlaylistId", 0);
        String name = getIntent().getStringExtra("PlaylistName");
        playlistSongsJoinViewModel = ViewModelProviders.of(this).get(PlaylistSongsJoinViewModel.class);
        playlistSongsJoinViewModel.getSongsFromPlaylist(id)
                .observe(this, new Observer<List<Song>>() {
                    @Override
                    public void onChanged(List<Song> songs) {
                        adapter.setMusicList(songs);
                    }
                });
//        musics = Music.createMusicsList(30);
        TextView tv = findViewById(R.id.playlistName);
        tv.setText(name);
        rv = findViewById(R.id.recyclerPlaylist);
        // TODO : maybe find a way to refactor this because its the same code as in HomeActivity.setMusicClickListener()
        adapter.setOnItemClickListener(new SongAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext() , PlayerActivity.class);
                intent.putExtra("MusicName", adapter.getMusics().get(position).getMusicName());
                intent.putExtra("MusicArtist", adapter.getMusics().get(position).getArtist());
                intent.putExtra("isLiked", adapter.getMusics().get(position).isLiked());
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
