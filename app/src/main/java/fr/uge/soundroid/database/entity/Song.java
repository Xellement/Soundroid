package fr.uge.soundroid.database.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Artist.class,
                parentColumns = "artistId",
                childColumns = "artist_id",
                onDelete = ForeignKey.NO_ACTION),
        @ForeignKey(entity = Album.class,
                parentColumns = "albumId",
                childColumns = "album_id",
                onDelete = ForeignKey.NO_ACTION)
})
public class Song {
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

    @ColumnInfo(name = "artist_id", index = true)
    public int songArtistId;

    @ColumnInfo(name = "album_id", index = true)
    public int songAlbumId;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (! (obj instanceof Song)) {
            return false;
        }
        Song s = (Song) obj;
        return s.songTitle.equals(songTitle) && s.songDuration == songDuration &&
                s.songArtistId == songArtistId && s.songAlbumId == songAlbumId;
    }
}