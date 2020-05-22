package fr.uge.soundroid.database.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import fr.uge.soundroid.database.entity.Song;
import fr.uge.soundroid.database.repository.SongRepository;

public class SongViewModel extends AndroidViewModel {
    private SongRepository songRepository;

    public SongViewModel(Application application) {
        super(application);
        songRepository = new SongRepository(application);
    }

    public LiveData<List<Song>> getAll() {
        return songRepository.getAll();
    }

    public LiveData<Song> findByName(String title) {
        return songRepository.findByName(title);
    }

    public LiveData<Song> findById(long id) {
        return songRepository.findById(id);
    }

    public LiveData<List<Song>> findLikeName(String title) {
        return songRepository.findLikeName(title);
    }

    public LiveData<List<Song>> findByArtist(String artist) {
        return songRepository.findByArtist(artist);
    }

    public LiveData<List<Song>> findByAlbum(String album) {
        return songRepository.findByAlbum(album);
    }

    public void insert(Song song) {
        songRepository.insert(song);
    }

    public void update(Song song) {
        songRepository.update(song);
    }

    public void delete(Song song) {
        songRepository.delete(song);
    }
}
