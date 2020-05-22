package fr.uge.soundroid.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.PlaylistSongsJoin;
import fr.uge.soundroid.database.entity.Song;
import fr.uge.soundroid.database.repository.PlaylistSongsJoinRepository;

public class PlaylistSongsJoinViewModel extends AndroidViewModel {
    private PlaylistSongsJoinRepository playlistSongsJoinRepository;

    public PlaylistSongsJoinViewModel(@NonNull Application application) {
        super(application);
        playlistSongsJoinRepository = new PlaylistSongsJoinRepository(application);
    }

    public LiveData<List<Song>> getSongsFromPlaylist(long playlistId) {
        return playlistSongsJoinRepository.getSongsFromPlaylist(playlistId);
    }

    public LiveData<List<Playlist>> getPlaylistsFromSong(long songId) {
        return playlistSongsJoinRepository.getPlaylistsFromSong(songId);
    }

    public void insert(PlaylistSongsJoin playlistSongsJoin) {
        playlistSongsJoinRepository.insert(playlistSongsJoin);
    }

    public void update(PlaylistSongsJoin playlistSongsJoin) {
        playlistSongsJoinRepository.update(playlistSongsJoin);
    }

    public void delete(PlaylistSongsJoin playlistSongsJoin) {
        playlistSongsJoinRepository.delete(playlistSongsJoin);
    }
}
