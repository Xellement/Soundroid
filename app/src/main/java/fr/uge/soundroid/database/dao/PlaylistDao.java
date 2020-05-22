package fr.uge.soundroid.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;

import fr.uge.soundroid.database.entity.Playlist;

@Dao
public interface PlaylistDao {
    @Insert
    long insert(Playlist playlist);

    @Update
    void update(Playlist playlist);
}
