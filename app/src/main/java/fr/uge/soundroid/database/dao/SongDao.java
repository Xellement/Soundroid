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

    @Query("SELECT songId, song.title, duration, tag, mark, artist_id, album_id" +
            " FROM song, artist where song.artist_id=artistId AND artist.name=:artistName")
    List<Song> findByArtist(String artistName);

    @Query("SELECT songId, song.title, duration, tag, mark, artist_id, album_id" +
            " FROM song, album where song.album_id=album.albumId AND album.title=:albumTitle")
    List<Song> findByAlbum(String albumTitle);

    @Insert
    void insert(Song song);

    @Delete
    void delete(Song song);
}