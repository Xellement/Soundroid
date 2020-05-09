package fr.uge.soundroid.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SongDao {
    @Query("SELECT * FROM song")
    List<Song> getAll();

    @Query("SELECT * FROM song WHERE title=:title")
    Song findByName(String title);

    @Query("SELECT * FROM song, artist where song.artist_id=artist.id AND artist.name=:artistName")
    List<Song> findByArtist(String artistName);

    @Query("SELECT * FROM song, album where song.album_id=album.id AND album.title=:albumTitle")
    List<Song> findByAlbum(String albumTitle);

    @Insert
    void insert(Song song);

    @Delete
    void delete(Song song);
}