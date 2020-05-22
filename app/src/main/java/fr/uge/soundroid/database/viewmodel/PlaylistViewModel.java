package fr.uge.soundroid.database.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.repository.PlaylistRepository;

public class PlaylistViewModel extends AndroidViewModel {
    private PlaylistRepository playlistRepository;

    public PlaylistViewModel(Application application) {
        super(application);
        playlistRepository = new PlaylistRepository(application);
    }

    public LiveData<Playlist> findByName(String name) {
        return playlistRepository.findByName(name);
    }

    public LiveData<Playlist> findById(long id) {
        return playlistRepository.findById(id);
    }

    public void insert(Playlist playlist) {
        playlistRepository.insert(playlist);
    }

    public void update(Playlist playlist) {
        playlistRepository.update(playlist);
    }

    public void delete(Playlist playlist) {
        playlistRepository.delete(playlist);
    }
}
