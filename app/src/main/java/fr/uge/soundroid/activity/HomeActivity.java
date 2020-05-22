package fr.uge.soundroid.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import fr.uge.soundroid.Music;
import fr.uge.soundroid.MusicAdapter;
import fr.uge.soundroid.IndexService;
import fr.uge.soundroid.Playlist;
import fr.uge.soundroid.PlaylistAdapter;
import fr.uge.soundroid.R;

public class HomeActivity extends AppCompatActivity {

    private ArrayList<Playlist> playlists, favoris;
    private ArrayList<Music> musics;
    private RecyclerView playlistRV, favorisRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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

        playlists = Playlist.createPlaylistsList(15, "playlist_icon.png", "Playlist");
        favoris = Playlist.createFavorisList();

        playlistRV = findViewById(R.id.mainRecycler);
        PlaylistAdapter adapter = new PlaylistAdapter(playlists);
        playlistRV.setAdapter(adapter);
        updateLayoutManager(playlistRV, 3);

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
                TextView tv = findViewById(R.id.title);
                tv.setText(p.getName());
                if (p.getName().equals("Récents") || p.getName().equals("Historique")) {
                    musics = Music.createMusicsList(15);
                    playlistRV.setAdapter(new MusicAdapter(musics));
                    updateLayoutManager(playlistRV, 1);
                } else {
                    playlists = Playlist.createPlaylistsList(15, p.getPathIcon(), p.getName());
                    playlistRV.setAdapter(new PlaylistAdapter(playlists));
                    updateLayoutManager(playlistRV, 3);
                }
            }
        });


        Thread index = new Thread(new Runnable() {
            @Override
            public void run() {
                IndexService indexService = new IndexService(HomeActivity.this.getApplication());
                try {
                    indexService.addMusicFilesFromRoot();
                } catch (NoSuchAlgorithmException e) {
                    Log.e("Algorithm not found", "MD5 algorithm not found");
                }
            }
        });
        index.start();
    }

    private void updateLayoutManager(RecyclerView rv, int span) {
        rv.setLayoutManager(new GridLayoutManager(this, span, GridLayoutManager.VERTICAL, false));
    }
}
