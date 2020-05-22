package fr.uge.soundroid.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import fr.uge.soundroid.database.SoundroidDatabase;
import fr.uge.soundroid.database.dao.SongDao;
import fr.uge.soundroid.database.entity.Song;

public class SongRepository {
    private SongDao songDao;

    public SongRepository(Application application) {
        songDao = SoundroidDatabase.getDatabase(application).songDao();
    }

    public LiveData<List<Song>> getAll() {
        return songDao.getAll();
    }

    public LiveData<Song> findByName(String title) {
        return songDao.findByName(title);
    }

    public LiveData<Song> findById(long id) {
        return songDao.findById(id);
    }

    public LiveData<List<Song>> findLikeName(String title) {
        return songDao.findLikeName(title);
    }

    public LiveData<List<Song>> findByArtist(String artist) {
        return songDao.findByArtist(artist);
    }

    public LiveData<List<Song>> findByAlbum(String album) {
        return songDao.findByAlbum(album);
    }

    public void insert(final Song song) {
        SoundroidDatabase.dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                songDao.insert(song);
            }
        });
    }

    public void update(final Song song) {
        SoundroidDatabase.dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                songDao.update(song);
            }
        });
    }

    public void delete(final Song song) {
        SoundroidDatabase.dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                songDao.delete(song);
            }
        });
    }
}
