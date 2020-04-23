package fr.uge.soundroid.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Artist {
    @PrimaryKey(autoGenerate = true)
    public int artistId;

    @ColumnInfo(name = "name")
    public String artistName;
}
