package fr.uge.soundroid.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"playlistId", "songId"})
public class PlaylistSongsCrossRef {
    public int playlistId;
    @ColumnInfo(index = true)
    public int songId;
}
