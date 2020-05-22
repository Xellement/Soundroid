package fr.uge.soundroid.database.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

@Entity
public class Song implements Serializable {

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

    private transient Bitmap cachedIconBitmap;
    private transient Bitmap cachedLikeBitmap;
    private String pathIcon;
    private String pathLike;

    public Song() {}

    public Song(String title, long duration, String tag, String artist, String album, String hash
            , String path) {
        songTitle = title;
        songDuration = duration;
        songTag = tag;
        artistName = artist;
        albumName = album;
        this.path = path;
        songHash = hash;
        liked = false;
        pathIcon = "music_icon.png";

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

    public String getMusicName() {
        return songTitle;
    }

    public String getArtist() {
        return artistName;
    }

    public Bitmap getBitmapIcon(Context context) {
        if (cachedIconBitmap == null) {
            try (InputStream is = context.getAssets().open(pathIcon)){
                cachedIconBitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                Log.e(String.valueOf(Log.ERROR), "Error bitmap icon");
                e.printStackTrace();
            }
        }
        return cachedIconBitmap;
    }

    public Bitmap getBitmapLike(Context context) {
        if (cachedLikeBitmap == null) {
            try (InputStream is = context.getAssets().open(pathLike)){
                cachedLikeBitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                Log.e(String.valueOf(Log.ERROR), "Error bitmap");
                e.printStackTrace();
            }
        }
        return cachedLikeBitmap;
    }
}