package fr.uge.soundroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Playlist {

    private String name, pathIcon;
    private transient Bitmap cachedBitmap;

    public Playlist(String name, String pathIcon) {
        System.out.println(pathIcon);
        this.name = name;
        this.pathIcon = pathIcon;
    }

    public String getName() {
        return name;
    }

    public String getPathIcon() {
        return pathIcon;
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

    /**
     * Methode Test GUI
     * @return
     */
    public static ArrayList<Playlist> createPlaylistsList(int size, String path, String name) {
        ArrayList<Playlist> playlists = new ArrayList<>();
        for (int i = 1; i <= size; i++)
            playlists.add(new Playlist(name+" n°"+i, path));
        return playlists;
    }

    public static ArrayList<Playlist> createFavorisList() {
        ArrayList<Playlist> favoris = new ArrayList<>();
        favoris.add(new Playlist("Playlist", "playlist_icon.png"));
        favoris.add(new Playlist("Récents", "music_icon.png"));
        favoris.add(new Playlist("Historique", "music_icon.png"));
        favoris.add(new Playlist("Artist", "artist_icon.png"));
        favoris.add(new Playlist("Album", "album_icon.png"));
        return favoris;
    }

}
