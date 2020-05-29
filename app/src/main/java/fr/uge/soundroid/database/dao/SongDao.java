package fr.uge.soundroid.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.uge.soundroid.database.entity.Song;

@Dao
public interface SongDao {
    @Query("SELECT * FROM song " +
            "ORDER BY song_number")
    LiveData<List<Song>> getAll();

    @Query("DELETE FROM song")
    void deleteAll();

    @Query("SELECT * FROM song WHERE title=:title ")
    LiveData<Song> findByName(String title);

    @Query("SELECT * FROM song WHERE songId = :id")
    LiveData<Song> findById(long id);

    @Query("SELECT DISTINCT * FROM song WHERE title LIKE '%' || :title || '%' ")
    LiveData<List<Song>> findLikeName(String title);

    @Query("SELECT DISTINCT songId, title, duration, tag, liked, artist_name, album_name, hash, path, icon_path, like_icon_path, song_number " +
            " FROM song where song.artist_name LIKE '%' || :artistName || '%' ")
    LiveData<List<Song>> findByArtist(String artistName);

    @Query("SELECT DISTINCT songId, title, duration, tag, liked, artist_name, album_name, hash, path, icon_path, like_icon_path, song_number " +
            "FROM song where song.album_name LIKE '%' || :albumTitle || '%' ")
    LiveData<List<Song>> findByAlbum(String albumTitle);

    @Query("SELECT DISTINCT artist_name FROM song")
    LiveData<List<String>> getArtistsName();

    @Query("SELECT DISTINCT album_name FROM song")
    LiveData<List<String>> getAlbumsName();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Song song);

    @Update
    void update(Song song);

    @Delete
    void delete(Song song);
}