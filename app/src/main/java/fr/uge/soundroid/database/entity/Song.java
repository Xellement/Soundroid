package fr.uge.soundroid.database.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
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

    @Ignore
    private transient Bitmap cachedIconBitmap;
    @Ignore
    private transient Bitmap cachedLikeBitmap;

    @ColumnInfo(name = "icon_path")
    private String pathIcon;

    @Ignore
    private byte[] picture;

    @ColumnInfo(name = "like_icon_path")
    private String pathLike;

    public String getPathIcon(){
        return pathIcon;
    }

    public String getPathLike() {
        return pathLike;
    }

    public void setPathIcon(String path) {
        pathIcon = path;
    }

    public void setPathLike(String path) {
        pathLike = path;
    }

    public Song() {}

    public Song(String title, long duration, String tag, String artist, String album, String hash
            , String path, byte[] picture) {
        songTitle = title;
        songDuration = duration;
        songTag = tag;
        artistName = artist;
        albumName = album;
        this.path = path;
        songHash = hash;
        liked = false;
        this.picture = picture;
        pathIcon = "music_icon.png";
        Log.d("SongCreation", "Created song " + songTitle + " - "  + pathIcon);
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

    public boolean isLiked() {
        return liked;
    }

    public Bitmap getBitmapIcon(Context context) {
        if (cachedIconBitmap == null) {
            if (picture != null) {
                Log.d(songTitle, "Picture isnt null");
                cachedIconBitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            }
            else {
                Log.d(songTitle, "Picture is null");
                try (InputStream is = context.getAssets().open(pathIcon)) {
                    cachedIconBitmap = BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    Log.e(String.valueOf(Log.ERROR), "Error bitmap icon");
                    e.printStackTrace();
                }
            }
        }
        return cachedIconBitmap;
    }

    public Bitmap getBitmapLike(Context context) {
        if (cachedLikeBitmap == null) {
            String likeIcon = (liked) ? "like.png" : "nolike.png";
            try (InputStream is = context.getAssets().open(likeIcon)){
                cachedLikeBitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                Log.e(String.valueOf(Log.ERROR), "Error bitmap");
                e.printStackTrace();
            }
        }
        return cachedLikeBitmap;
    }
}