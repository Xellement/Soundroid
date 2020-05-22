package fr.uge.soundroid.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import fr.uge.soundroid.IndexService;
import fr.uge.soundroid.Playlist;
import fr.uge.soundroid.PlaylistAdapter;
import fr.uge.soundroid.R;

public class HomeActivity extends AppCompatActivity {

    private ArrayList<Playlist> playlists, favoris;

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

        RecyclerView playlistRV = findViewById(R.id.recyclerPlaylist);
        playlists = Playlist.createPlaylistsList(Color.rgb(45, 45, 107), 15);

        PlaylistAdapter adapter = new PlaylistAdapter(playlists);

        playlistRV.setAdapter(adapter);
        playlistRV.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));

        RecyclerView favorisRV = findViewById(R.id.recyclerFavoris);

        PlaylistAdapter adapterFav = new PlaylistAdapter(playlists);

        favorisRV.setAdapter(adapterFav);
        favorisRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


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
}
