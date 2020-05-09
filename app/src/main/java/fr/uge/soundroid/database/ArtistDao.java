package fr.uge.soundroid.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArtistDao {
    @Query("SELECT * FROM artist")
    List<Artist> getAll();

    @Query("SELECT * FROM artist WHERE artist.name=:name")
    Artist findByName(String name);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Artist artist);

    @Delete
    void delete(Artist artist);
}
