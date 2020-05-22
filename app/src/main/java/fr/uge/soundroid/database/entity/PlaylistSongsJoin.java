package fr.uge.soundroid.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "playlist_songs_join",
        primaryKeys = {"playlistId", "songId"},
        foreignKeys = {
                @ForeignKey(entity = Song.class,
                        parentColumns = "songId",
                        childColumns = "songId"),
                @ForeignKey(entity = Playlist.class,
                        parentColumns = "playlistId",
                        childColumns = "playlistId")
        }
)
public class PlaylistSongsJoin {
    @ColumnInfo(index = true)
    public long playlistId;
    @ColumnInfo(index = true)
    public long songId;

    public PlaylistSongsJoin(long playlistId, long songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }
}
