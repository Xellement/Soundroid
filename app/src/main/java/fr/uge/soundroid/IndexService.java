package fr.uge.soundroid;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IndexService {
    private List<String> musicFilesList = new ArrayList<>();
    // TODO: Replace this shit by database interactions

    public void addMusicFilesFromRoot() {
        musicFilesList.clear();
        addMusicFilesFrom(Environment.getExternalStoragePublicDirectory("").getAbsolutePath());
        Log.d("Music List", musicFilesList.toString());
    }

    private void addMusicFilesFrom(String dirPath) {
        final File dir = new File(dirPath);
        final File[] files = dir.listFiles();
        for (File file: files) {
            if (file.isDirectory()) {
                addMusicFilesFrom(file.getAbsolutePath());
            } else if (file.isFile() && file.getPath().endsWith(".mp3")) {
                musicFilesList.add(file.getAbsolutePath());
            } else {
                // log something ?
            }
        }
    }
}
