package fr.uge.soundroid.database.entity;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Artist {
    @PrimaryKey(autoGenerate = true)
    public int artistId;

    @ColumnInfo(name = "name")
    public String artistName;

    public Artist() {
    }

    public Artist(String name) {
        artistName = name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (! (obj instanceof Artist)) {
            return false;
        }
        Artist a = (Artist) obj;
        Log.d("equals", a.artistId + " " + artistId + " " + a.artistName + " " + artistName);
        return a.artistName.equals(artistName);
    }
}
