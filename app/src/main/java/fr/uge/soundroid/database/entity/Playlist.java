package fr.uge.soundroid.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Playlist {
    @PrimaryKey(autoGenerate = true)
    public long playlistId;

    @ColumnInfo(name = "name")
    public String playlistName;

    public Playlist() {}

    public Playlist(String name) {
        playlistName = name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (! (obj instanceof Playlist)) {
            return false;
        }
        Playlist p = (Playlist) obj;
        return p.playlistId == playlistId && p.playlistName.equals(playlistName);
    }

    @NonNull
    @Override
    public String toString() {
        return "id: " + playlistId + " - name: " + playlistName;
    }
}
