package fr.uge.soundroid.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import fr.uge.soundroid.database.SoundroidDatabase;
import fr.uge.soundroid.database.dao.PlaylistSongsJoinDao;
import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.PlaylistSongsJoin;
import fr.uge.soundroid.database.entity.Song;

public class PlaylistSongsJoinRepository {
    private PlaylistSongsJoinDao playlistSongsJoinDao;

    public PlaylistSongsJoinRepository(Application application) {
        playlistSongsJoinDao = SoundroidDatabase.getDatabase(application).playlistSongsJoinDao();
    }

    public LiveData<List<Song>> getSongsFromPlaylist(long playlistId) {
        return playlistSongsJoinDao.getSongsFromPlaylist(playlistId);
    }

    public LiveData<List<Playlist>> getPlaylistsFromSong(long songId) {
        return playlistSongsJoinDao.getPlaylistsFromSong(songId);
    }

    public void insert(final PlaylistSongsJoin playlistSongsJoin) {
        SoundroidDatabase.dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistSongsJoinDao.insert(playlistSongsJoin);
            }
        });
    }

    public void update(final PlaylistSongsJoin playlistSongsJoin) {
        SoundroidDatabase.dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistSongsJoinDao.update(playlistSongsJoin);
            }
        });
    }

    public void delete(final PlaylistSongsJoin playlistSongsJoin) {
        SoundroidDatabase.dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistSongsJoinDao.update(playlistSongsJoin);
            }
        });
    }

    public void deleteAll() {
        SoundroidDatabase.dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistSongsJoinDao.deleteAll();
            }
        });
    }
}
