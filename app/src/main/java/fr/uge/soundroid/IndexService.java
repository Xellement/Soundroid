package fr.uge.soundroid;

import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class IndexService {
    private List<String> musicFilesList = new ArrayList<>();
    private HashSet<String> titles = new HashSet<>();
    private HashSet<String> artists = new HashSet<>();
    private HashSet<String> albums = new HashSet<>();
    private HashSet<String> genres = new HashSet<>();
    // TODO: Replace this shit by database interactions

    public void addMusicFilesFromRoot() {
        musicFilesList.clear();
        addMusicFilesFrom(Environment.getExternalStoragePublicDirectory("").getAbsolutePath());
        Log.d("Music List", musicFilesList.toString());
        Log.d("TITLES", titles.toString());
        Log.d("ARTISTS", artists.toString());
        Log.d("ALBUMS", albums.toString());
        Log.d("GENRES", genres.toString());
    }

    private void addMusicFilesFrom(String dirPath) {
        final File dir = new File(dirPath);
        final File[] files = dir.listFiles();
        for (File file: files) {
            if (file.isDirectory()) {
                addMusicFilesFrom(file.getAbsolutePath());
            } else if (file.isFile() && file.getPath().endsWith(".mp3")) {
                MediaMetadataRetriever mtr = new MediaMetadataRetriever();
                mtr.setDataSource(file.getAbsolutePath());
                titles.add(mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                artists.add(mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                albums.add(mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                genres.add(mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
                musicFilesList.add(file.getAbsolutePath());
            }
        }
    }
}
