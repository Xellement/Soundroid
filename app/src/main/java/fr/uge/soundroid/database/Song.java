package fr.uge.soundroid.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Artist.class,
                parentColumns = "id",
                childColumns = "artist_id",
                onDelete = ForeignKey.NO_ACTION),
        @ForeignKey(entity = Album.class,
                parentColumns = "id",
                childColumns = "album_id",
                onDelete = ForeignKey.NO_ACTION)
})
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

    @ColumnInfo(name = "artist_id")
    public int artistId;

    @ColumnInfo(name = "album_id")
    public int albumId;
}