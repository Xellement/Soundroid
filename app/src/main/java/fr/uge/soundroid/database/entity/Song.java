package fr.uge.soundroid.database.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Artist.class,
                parentColumns = "artistId",
                childColumns = "artist_id",
                onDelete = ForeignKey.SET_NULL,
                onUpdate = ForeignKey.CASCADE),
        @ForeignKey(entity = Album.class,
                parentColumns = "albumId",
                childColumns = "album_id",
                onDelete = ForeignKey.SET_NULL,
                onUpdate = ForeignKey.CASCADE)
},
        indices = {
            @Index("artist_id"), @Index("album_id")
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

    @ColumnInfo(name="liked")
    public boolean liked;

    @ColumnInfo(name = "artist_id")
    public int songArtistId;

    @ColumnInfo(name = "album_id")
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