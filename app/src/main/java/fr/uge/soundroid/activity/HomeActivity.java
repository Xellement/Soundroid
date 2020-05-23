package fr.uge.soundroid.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.uge.soundroid.IndexService;
import fr.uge.soundroid.PlaylistAdapter;
import fr.uge.soundroid.R;
import fr.uge.soundroid.SongAdapter;
import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.Song;
import fr.uge.soundroid.database.viewmodel.PlaylistViewModel;
import fr.uge.soundroid.database.viewmodel.SongViewModel;

//import fr.uge.soundroid.Playlist;

public class HomeActivity extends AppCompatActivity {

    private ArrayList<Playlist> playlists = new ArrayList<>();
    private ArrayList<Playlist> favorites;
    private ArrayList<Song> musics = new ArrayList<>();
    private RecyclerView playlistRV, favoritesRV;
    private SongViewModel songViewModel;
    private PlaylistViewModel playlistViewModel;
    private PlaylistAdapter playlistAdapter = new PlaylistAdapter(playlists);
    private PlaylistAdapter favoritesAdapter;
    private SongAdapter songAdapter = new SongAdapter(musics);
    private IndexService indexService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);
        playlistViewModel = ViewModelProviders.of(this).get(PlaylistViewModel.class);

        songViewModel.getAll().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                songAdapter.setMusicList(songs);
            }
        });

        playlistViewModel.getAll().observe(this, new Observer<List<Playlist>>() {
            @Override
            public void onChanged(List<Playlist> playlists) {
                playlistAdapter.setPlaylists(playlists);
            }
        });


//        playlists = Playlist.createPlaylistsList(15, "playlist_icon.png", "Playlist");
        favorites = Playlist.createFavoritesList();

        playlistRV = findViewById(R.id.mainRecycler);
//        PlaylistAdapter adapter = new PlaylistAdapter(playlists);
        setPlaylistListenerClick(playlistAdapter);
        playlistRV.setAdapter(playlistAdapter);
        updateLayoutManager(playlistRV, 3);

        favoritesRV = findViewById(R.id.recyclerFavoris);
        favoritesAdapter = new PlaylistAdapter(favorites);
        setPlaylistListenerClick(favoritesAdapter);
        favoritesRV.setAdapter(favoritesAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        llm.scrollToPositionWithOffset(0, -1);
        favoritesRV.setLayoutManager(llm);

        favoritesAdapter.setOnItemClickListener(new PlaylistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Playlist p = favorites.get(position);
                TextView tv = findViewById(R.id.title);
                tv.setText(p.getName());
                if (p.getName().equals("Récents") || p.getName().equals("Historique")) {
//                    musics = Music.createMusicsList(15);
//                    SongAdapter mscAdapter = new SongAdapter(musics);
                    setMusicListenerClick(songAdapter);
                    playlistRV.setAdapter(songAdapter);
                    updateLayoutManager(playlistRV, 1);
                } else {
                    // TODO : create db query to compute a List of artists and a list of album
                    // TODO : (both will in fact be playlists, but we should get the right names)
//                    playlists = Playlist.createPlaylistsList(15, p.getPathIcon(), p.getName());
//                    PlaylistAdapter pltAdapter = new PlaylistAdapter(playlists);
                    setPlaylistListenerClick(playlistAdapter);
                    playlistRV.setAdapter(playlistAdapter);
                    updateLayoutManager(playlistRV, 3);
                }
            }
        });
    }

    private static final String[] PERMISSIONS = {
        Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final int REQUEST_PERMISSIONS = 1;

    private static final int PERMISSIONS_COUNT = 1;

    @SuppressLint("NewApi")
    private boolean arePermissionsDenied() {
        for (int i = 0; i < PERMISSIONS_COUNT; ++i) {
            if (checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (arePermissionsDenied()) {
            ((ActivityManager) (this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
            recreate();
        } else {
            onResume();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }
        // TODO : Create a Handler and use it to run indexService periodically
        // -> we may need to store the date of last indexation in DB
        if (indexService != null) {
            return;
        }
        Thread index = new Thread(new Runnable() {
            @Override
            public void run() {
                indexService = new IndexService(HomeActivity.this.getApplication());
                indexService.addMusicFilesFromRoot();
            }
        });
        index.start();
    }

    private void setPlaylistListenerClick(PlaylistAdapter adap){
        adap.setOnItemClickListener(new PlaylistAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                TextView tv = findViewById(R.id.title);
                System.out.println("Playlist : " + tv.getText());
                Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
                intent.putExtra("PlaylistId", playlistAdapter.getPlaylists().get(position).getPlaylistId());
                intent.putExtra("PlaylistName", playlistAdapter.getPlaylists().get(position).playlistName);
                startActivity(intent);
            }
        });
    }

    private void setMusicListenerClick(SongAdapter adap){
        adap.setOnItemClickListener(new SongAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                TextView tv = findViewById(R.id.title);
                System.out.println("Music : " + tv.getText());
                Intent intent = new Intent(getApplicationContext() , PlayerActivity.class);
                intent.putExtra("MusicName", songAdapter.getMusics().get(position).getMusicName());
                intent.putExtra("MusicArtist", songAdapter.getMusics().get(position).getArtist());
                intent.putExtra("isLiked", songAdapter.getMusics().get(position).isLiked());
                startActivity(intent);
            }
        });
    }

    private void updateLayoutManager(RecyclerView rv, int span) {
        rv.setLayoutManager(new GridLayoutManager(this, span, GridLayoutManager.VERTICAL, false));
    }
}
