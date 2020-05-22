package fr.uge.soundroid.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import fr.uge.soundroid.database.entity.Song;

@Dao
public interface SongDao {
    @Query("SELECT * FROM song")
    List<Song> getAll();

    @Query("SELECT * FROM song WHERE title=:title")
    Song findByName(String title);

    @Query("SELECT songId, title, duration, tag, liked, artist_name, album_name" +
            " FROM song where song.artist_name = :artistName")
    List<Song> findByArtist(String artistName);

    @Query("SELECT songId, title, duration, tag, liked, artist_name, album_name" +
            " FROM song where song.album_name = :albumTitle")
    List<Song> findByAlbum(String albumTitle);

    @Insert
    long insert(Song song);

    @Delete
    void delete(Song song);
}