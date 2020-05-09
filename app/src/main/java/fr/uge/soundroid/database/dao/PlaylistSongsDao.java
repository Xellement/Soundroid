package fr.uge.soundroid.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import fr.uge.soundroid.database.PlaylistSongs;

@Dao
public interface PlaylistSongsDao {
    @Transaction
    @Query("SELECT * FROM playlist")
    List<PlaylistSongs> getPlaylistWithSongs();
}
