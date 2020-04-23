package fr.uge.soundroid.database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

interface SongDao {
    @Query("SELECT * FROM song")
    List<Song> getAll();

    @Query("SELECT * FROM song WHERE title=:title")
    Song findByName(String title);

    @Insert
    void insert(Song song);

    @Delete
    void delete(Song song);
}