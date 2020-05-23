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
import java.util.ArrayList;

@Entity
public class Playlist {
    @PrimaryKey(autoGenerate = true)
    public long playlistId;

    @ColumnInfo(name = "name")
    public String playlistName;

    @Ignore
    private transient Bitmap cachedBitmap;
    @Ignore
    private final static String DEFAULT_ICON = "playlist_icon.png";
    private String pathIcon;

    public Playlist() {}

    public Playlist(String name) {
        this(name, DEFAULT_ICON);
    }

    public Playlist(String name, String pathIcon) {
        playlistName = name;
        this.pathIcon = pathIcon;
    }

    public String getName() {
        return playlistName;
    }

    public String getPathIcon() {
        return pathIcon;
    }

    public void setPathIcon(String path) {
        pathIcon = path;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public Bitmap getBitmap(Context context) {
        if (cachedBitmap == null) {
            try (InputStream is = context.getAssets().open(pathIcon)){
                cachedBitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                Log.e(String.valueOf(Log.ERROR), "Error bitmap");
                e.printStackTrace();
            }
        }
        return cachedBitmap;
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

    public static ArrayList<Playlist> createFavoritesList() {
        ArrayList<Playlist> favorites = new ArrayList<>();
        favorites.add(new Playlist("Playlist", "playlist_icon.png"));
        favorites.add(new Playlist("Récents", "music_icon.png"));
        favorites.add(new Playlist("Historique", "music_icon.png"));
        favorites.add(new Playlist("Artist", "artist_icon.png"));
        favorites.add(new Playlist("Album", "album_icon.png"));
        return favorites;
    }
}
