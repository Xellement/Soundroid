package fr.uge.soundroid.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "playlist_songs_crossref",
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
public class PlaylistSongsCrossRef {
    public int playlistId;
    public int songId;

    public PlaylistSongsCrossRef(int playlistId, int songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }
}
