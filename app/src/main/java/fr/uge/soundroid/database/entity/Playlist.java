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

    @ColumnInfo(name = "playlist_artist")
    public String playlistArtist;

    @ColumnInfo(name = "icon_path")
    private String pathIcon;

    @Ignore
    private transient Bitmap cachedBitmap;
    @Ignore
    private final static String DEFAULT_ICON = "playlist_icon.png";
    @Ignore
    private final static String DEFAULT_ARTIST = "Various";


    public Playlist() {
    }

    public Playlist(String name, int type) {
        this(name, DEFAULT_ICON, type, DEFAULT_ARTIST);
    }

    public Playlist(String name, String pathIcon, int type) {
        this(name, pathIcon, type, DEFAULT_ARTIST);
    }

    public Playlist(String name, int type, String artist) {
        this(name, DEFAULT_ICON, type, DEFAULT_ARTIST);
    }

    public Playlist(String name, String pathIcon, int type, String artist) {
        if (type < 0 || type > 2) {
            throw new IllegalArgumentException();
        }
        playlistName = name;
        this.pathIcon = pathIcon;
        playlistType = type;
        playlistArtist = artist;
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

    public String getPlaylistArtist() {
        return playlistArtist;
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
                Log.e(String.valueOf(Log.ERROR), "Error bitmap" + e.toString());
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
