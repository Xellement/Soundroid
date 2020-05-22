package fr.uge.soundroid.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import fr.uge.soundroid.database.entity.Playlist;

@Dao
public interface PlaylistDao {
    @Insert
    long insert(Playlist playlist);

    @Update
    void update(Playlist playlist);

    @Delete
    void delete(Playlist playlist);

    @Query("SELECT * FROM playlist WHERE name = :name")
    LiveData<Playlist> findByName(String name);

    @Query("SELECT * FROM playlist WHERE playlistId = :id")
    LiveData<Playlist> findById(long id);
}
