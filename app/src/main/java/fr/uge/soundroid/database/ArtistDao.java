package fr.uge.soundroid.database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

interface ArtistDao {
    @Query("SELECT * FROM artist")
    List<Artist> getAll();

    @Query("SELECT * FROM artist WHERE name=:name")
    Artist findByName(String name);

    @Insert
    void insert(Artist artist);

    @Delete
    void delete(Artist artist);
}
