package fr.uge.soundroid.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import fr.uge.soundroid.database.SoundroidDatabase;
import fr.uge.soundroid.database.dao.PlaylistDao;
import fr.uge.soundroid.database.entity.Playlist;

public class PlaylistRepository {
    private PlaylistDao playlistDao;

    public PlaylistRepository(Application application) {
        playlistDao = SoundroidDatabase.getDatabase(application).playlistDao();
    }

    public LiveData<Playlist> findByName(String name) {
        return playlistDao.findByName(name);
    }

    public LiveData<Playlist> findById(long id) {
        return playlistDao.findById(id);
    }

    public LiveData<List<Playlist>> getAll() {
        return playlistDao.getAll();
    }

    public void insert(final Playlist playlist) {
        SoundroidDatabase.dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistDao.insert(playlist);
            }
        });
    }

    public void update(final Playlist playlist) {
        SoundroidDatabase.dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistDao.update(playlist);
            }
        });
    }

    public void delete(final Playlist playlist) {
        SoundroidDatabase.dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistDao.delete(playlist);
            }
        });
    }

    public void deleteAll() {
        SoundroidDatabase.dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistDao.deleteAll();
            }
        });
    }
}
