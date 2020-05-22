package fr.uge.soundroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Music {

    private String musicName, artist, pathIcon, pathLike;
    private boolean like;
    private transient Bitmap cachedIconBitmap, cachedLikeBitmap;

    public Music(String musicName, String artist) {
        this.musicName = musicName;
        this.artist = artist;
        pathIcon = "music_icon.png";
        pathLike = "nolike.png";
    }

    public String getMusicName() {
        return musicName;
    }
    public String getArtist() {
        return artist;
    }
    public String getPathIcon() {
        return pathIcon;
    }
    public boolean isLike() {
        return like;
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

    public static ArrayList<Music> createMusicsList(int size) {
        ArrayList<Music> musics = new ArrayList<>();
        for (int i = 1; i <= size; i++)
            musics.add(new Music("Music n°"+i, "Artist"));
        return musics;
    }

}
