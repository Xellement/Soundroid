package fr.uge.soundroid.database;

import android.app.Application;

import java.util.List;

import fr.uge.soundroid.database.dao.SongDao;
import fr.uge.soundroid.database.entity.Song;

public class SearchService {
    private SongDao songDao;

    public  SearchService(Application app) {
        songDao = SoundroidDatabase.getDatabase(app).songDao();
    }

    public List<Song> searchByTitle(String title) {
        return songDao.findLikeName(title);
    }

    public List<Song> searchByArtist(String artist) {
        return songDao.findByArtist(artist);
    }

    public List<Song> searchByAlbum(String album) {
        return songDao.findByAlbum(album);
    }

    public List<Song> search(String album, String artist, String genre, String[] keyWords) {
        return null;
    }
}
