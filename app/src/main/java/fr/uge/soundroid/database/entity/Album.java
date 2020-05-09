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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (! (obj instanceof Album)) {
            return false;
        }
        Album a = (Album) obj;
        return a.albumTitle.equals(albumTitle) && a.nbSongs == nbSongs;
    }
}
