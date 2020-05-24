package fr.uge.soundroid.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.uge.soundroid.database.entity.Playlist;

@Dao
public interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Playlist playlist);

    @Update
    void update(Playlist playlist);

    @Delete
    void delete(Playlist playlist);

    @Query("DELETE FROM playlist")
    void deleteAll();

    @Query("SELECT playlistId FROM playlist WHERE name = :name")
    long getIdByName(String name);

    @Query("SELECT * FROM playlist WHERE name = :name")
    LiveData<Playlist> findByName(String name);

    @Query("SELECT * FROM playlist WHERE playlistId = :id")
    LiveData<Playlist> findById(long id);

    @Query("SELECT * from playlist")
    LiveData<List<Playlist>> getAll();

    @Query("SELECT * from playlist WHERE playlist_type = 0")
    LiveData<List<Playlist>> getSongsPlaylists();

    @Query("SELECT * FROM playlist WHERE playlist_type = 1")
    LiveData<List<Playlist>> getArtistsPlaylists();

    @Query("SELECT * FROM playlist WHERE playlist_type = 2")
    LiveData<List<Playlist>> getAlbumsPlaylists();

}
