package fr.uge.soundroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class Playlist {

    private String name;

    private transient Bitmap cachedBitmap;

    public Playlist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Methode Test GUI
     * @return
     */
    public static ArrayList<Playlist> createPlaylistsList() {
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        for (int i = 1; i <= 10; i++)
            playlists.add(new Playlist("Playlit n°"+i));
        return playlists;
    }

}
