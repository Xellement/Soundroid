package fr.uge.soundroid.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
class Song {
    @PrimaryKey(autoGenerate = true)
    public int songId;

    @ColumnInfo(name="title")
    public String songTitle;

    @ColumnInfo(name="duration")
    public int songDuration;

    @ColumnInfo(name="tag")
    public String songTag;

    @ColumnInfo(name="mark")
    public int songMark; // From 0 to 5 ?
}