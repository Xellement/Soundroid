package fr.uge.soundroid.database.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Album {
    @PrimaryKey(autoGenerate = true)
    public int albumId;

    @ColumnInfo(name = "title")
    public String albumTitle;

    @ColumnInfo(name = "nb_songs")
    public int nbSongs;

    public Album() {
    }

    public Album(String title, int nbSongs) {
        albumTitle = title;
        this.nbSongs = nbSongs;
    }

    public Album(String title) {
        this(title, 0);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (! (obj instanceof Album)) {
            return false;
        }
        Album a = (Album) obj;
        return a.albumTitle.equals(albumTitle) && a.nbSongs == nbSongs;
    }
}
