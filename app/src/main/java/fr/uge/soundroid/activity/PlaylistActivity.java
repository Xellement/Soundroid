package fr.uge.soundroid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import fr.uge.soundroid.Music;
import fr.uge.soundroid.SongAdapter;
import fr.uge.soundroid.R;

public class PlaylistActivity extends AppCompatActivity {

    private ArrayList<Music> musics;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        musics = Music.createMusicsList(30);

        rv = findViewById(R.id.recyclerPlaylist);
        SongAdapter adapter = new SongAdapter(musics);
        rv.setAdapter(adapter);
        updateLayoutManager(rv, 1);
    }

    private void updateLayoutManager(RecyclerView rv, int span) {
        rv.setLayoutManager(new GridLayoutManager(this, span, GridLayoutManager.VERTICAL, false));
    }

}
