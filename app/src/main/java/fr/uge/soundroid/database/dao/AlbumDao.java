package fr.uge.soundroid.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import fr.uge.soundroid.database.entity.Album;

@Dao
public
interface AlbumDao {
    @Query("SELECT * FROM album")
    List<Album> getAll();

    @Query("SELECT * from album where title=:title")
    Album findByName(String title);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Album album);

    @Delete
    void delete(Album album);
}
