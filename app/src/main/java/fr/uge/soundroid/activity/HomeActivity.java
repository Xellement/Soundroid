package fr.uge.soundroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.uge.soundroid.PlaylistAdapter;
import fr.uge.soundroid.R;
import fr.uge.soundroid.SongAdapter;
import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.Song;
import fr.uge.soundroid.database.viewmodel.PlaylistViewModel;
import fr.uge.soundroid.database.viewmodel.SongViewModel;

public class HomeActivity extends AppCompatActivity {

    private List<Playlist> songsPlaylist = new ArrayList<>();
    private List<Playlist> favorites;
    private List<Playlist> artistsList;
    private List<Playlist> albumsList;
    private List<Song> musics = new ArrayList<>();
    private RecyclerView playlistRV, favoritesRV;
    private SongViewModel songViewModel;
    private PlaylistViewModel playlistViewModel;
    private PlaylistAdapter playlistAdapter = new PlaylistAdapter(songsPlaylist);
    private PlaylistAdapter favoritesAdapter;
    private SongAdapter songAdapter = new SongAdapter(musics);

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

        playlistViewModel.getSongsPlaylists().observe(this, new Observer<List<Playlist>>() {
            @Override
            public void onChanged(List<Playlist> playlists) {
                songsPlaylist = playlists;
            }
        });

        playlistViewModel.getArtistsPlaylists().observe(this, new Observer<List<Playlist>>() {
            @Override
            public void onChanged(List<Playlist> playlists) {
                artistsList = playlists;
            }
        });

        playlistViewModel.getAlbumsPlaylists().observe(this, new Observer<List<Playlist>>() {
            @Override
            public void onChanged(List<Playlist> playlists) {
                albumsList = playlists;
            }
        });



//        playlists = Playlist.createPlaylistsList(15, "playlist_icon.png", "Playlist");
        favorites = Playlist.createFavoritesList();

        playlistRV = findViewById(R.id.mainRecycler);
//        PlaylistAdapter adapter = new PlaylistAdapter(playlists);
        playlistAdapter.setPlaylists(songsPlaylist);
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
                    setMusicListenerClick(songAdapter);
                    playlistRV.setAdapter(songAdapter);
                    updateLayoutManager(playlistRV, 1);
                } else if (p.getName().equals("Artist")) {
                    updatePlaylistAdapter(artistsList);
                } else if (p.getName().equals("Album")) {
                    updatePlaylistAdapter(albumsList);
                } else {
                    updatePlaylistAdapter(songsPlaylist);
                }
            }
        });
    }

    private void updatePlaylistAdapter(List<Playlist> playlists) {
        playlistAdapter.setPlaylists(playlists);
        setPlaylistListenerClick(playlistAdapter);
        playlistRV.setAdapter(playlistAdapter);
        updateLayoutManager(playlistRV, 3);
    }

    private void setPlaylistListenerClick(final PlaylistAdapter adap){
        adap.setOnItemClickListener(new PlaylistAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                TextView tv = findViewById(R.id.title);
                System.out.println("Playlist : " + tv.getText());
                Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
                intent.putExtra("PlaylistId", adap.getPlaylists().get(position).getPlaylistId());
                intent.putExtra("PlaylistName", adap.getPlaylists().get(position).getName());
                intent.putExtra("PlaylistArtist", adap.getPlaylists().get(position).getPlaylistArtist());
                intent.putExtra("PlaylistIcon", adap.getPlaylists().get(position).getPathIcon());
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
