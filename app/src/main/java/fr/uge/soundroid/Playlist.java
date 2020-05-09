package fr.uge.soundroid;

import android.graphics.Color;

import java.util.ArrayList;

public class Playlist {

    private String name;
    private int color;

    public Playlist(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    /**
     * Methode Test GUI
     * @return
     */
    public static ArrayList<Playlist> createPlaylistsList(int color, int size) {
        ArrayList<Playlist> playlists = new ArrayList<>();
        for (int i = 1; i <= size; i++)
            playlists.add(new Playlist("Playlit n°"+i, color));
        return playlists;
    }

}
