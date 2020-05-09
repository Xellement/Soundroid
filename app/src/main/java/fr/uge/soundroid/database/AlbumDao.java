package fr.uge.soundroid.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
interface AlbumDao {
    @Query("SELECT * FROM album")
    List<Album> getALl();

    @Query("SELECT * from album where title=:title")
    Album findByName(String title);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Album album);

    @Delete
    void delete(Album album);
}
