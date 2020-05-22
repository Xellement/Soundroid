package fr.uge.soundroid;

import android.app.Application;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import fr.uge.soundroid.database.SoundroidDatabase;
import fr.uge.soundroid.database.dao.SongDao;
import fr.uge.soundroid.database.entity.Song;

public class IndexService {
    private SongDao songDao;

    public IndexService(Application app) {
        songDao = SoundroidDatabase.getDatabase(app).songDao();
    }

    public void addMusicFilesFromRoot() throws NoSuchAlgorithmException {
        songDao.deleteAll();
        addMusicFilesFrom(Environment.getExternalStoragePublicDirectory("").getAbsolutePath());
        Log.d("Song list", songDao.getAll().toString());
    }

    private void addMusicFilesFrom(String dirPath) throws NoSuchAlgorithmException {
        final File dir = new File(dirPath);
        final File[] files = dir.listFiles();
        assert files != null;
        for (File file: files) {
            if (file.isDirectory()) {
                addMusicFilesFrom(file.getAbsolutePath());
            } else if (file.isFile() && file.getPath().endsWith(".mp3")) {
                MediaMetadataRetriever mtr = new MediaMetadataRetriever();
                mtr.setDataSource(file.getAbsolutePath());

                final String title = mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                final String artist = mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                final String album = mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                final String genre = mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
                final String duration = mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                String s = title + artist + album + genre + duration;
                final String songHash = String.valueOf(s.hashCode());

                songDao.insert(new Song(title, Long.parseLong(duration), null, artist, album, songHash));
            }
        }
    }
}
