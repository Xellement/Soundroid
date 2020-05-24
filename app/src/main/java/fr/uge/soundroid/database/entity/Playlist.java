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
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@Entity(indices = {
        @Index(value = "name", unique = true)
})
public class Playlist {
    @PrimaryKey(autoGenerate = true)
    public long playlistId;

    @ColumnInfo(name = "name")
    public String playlistName;

    @ColumnInfo(name = "playlist_type")
    public int playlistType;    // 0 for classic playlist, 1 for artist playlist, 2 for album playlist

    @Ignore
    private transient Bitmap cachedBitmap;
    @Ignore
    private final static String DEFAULT_ICON = "playlist_icon.png";
    private String pathIcon;

    public Playlist() {
    }

    public Playlist(String name, int type) {
        this(name, DEFAULT_ICON, type);
    }

    public Playlist(String name, String pathIcon, int type) {
        if (type < 0 || type > 2) {
            throw new IllegalArgumentException();
        }
        playlistName = name;
        this.pathIcon = pathIcon;
        playlistType = type;
    }

    public String getName() {
        return playlistName;
    }

    public String getPathIcon() {
        return pathIcon;
    }

    public int getPlaylistType() {
        return playlistType;
    }

    public void setPathIcon(String path) {
        pathIcon = path;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public Bitmap getBitmap(Context context) {
        if (cachedBitmap == null) {
            try (InputStream is = context.getAssets().open(pathIcon)) {
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
        if (!(obj instanceof Playlist)) {
            return false;
        }
        Playlist p = (Playlist) obj;
        return p.playlistType == playlistType && p.playlistName.equals(playlistName);
    }

    @NonNull
    @Override
    public String toString() {
        return "id: " + playlistId + " - name: " + playlistName;
    }

    public static ArrayList<Playlist> createFavoritesList() {
        ArrayList<Playlist> favorites = new ArrayList<>();
        favorites.add(new Playlist("Playlist", "playlist_icon.png", 0));
        favorites.add(new Playlist("Récents", "music_icon.png", 0));
        favorites.add(new Playlist("Historique", "music_icon.png", 0));
        favorites.add(new Playlist("Artist", "artist_icon.png", 1));
        favorites.add(new Playlist("Album", "album_icon.png", 2));
        return favorites;
    }
}
