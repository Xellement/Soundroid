package fr.uge.soundroid.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Song {

    @PrimaryKey(autoGenerate = true)
    public long songId;

    @ColumnInfo(name="title")
    public String songTitle;

    @ColumnInfo(name="duration")
    public long songDuration;

    @ColumnInfo(name="tag")
    public String songTag;

    @ColumnInfo(name="liked")
    public boolean liked;

    @ColumnInfo(name = "artist_name")
    public String artistName;

    @ColumnInfo(name = "album_name")
    public String albumName;

    @ColumnInfo(name = "hash")
    public String songHash;

    @ColumnInfo(name = "path")
    public String path;

    public Song() {}


    public Song(String title, long duration, String tag, String artist, String album, String hash, String path) {
        songTitle = title;
        songDuration = duration;
        songTag = tag;
        artistName = artist;
        albumName = album;
        this.path = path;
        songHash = hash;
        liked = false;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (! (obj instanceof Song)) {
            return false;
        }
        Song s = (Song) obj;
        return s.songTitle.equals(songTitle) && s.songDuration == songDuration &&
                s.artistName.equals(artistName) && s.albumName.equals(albumName);
    }

    @NonNull
    @Override
    public String toString() {
        return "[ Title: " + songTitle + "; Artist: " + artistName + "; Album: " + albumName + "; Duration: " + songDuration + "ms; Hash: " + songHash + " ]";
    }
}