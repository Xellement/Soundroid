package fr.uge.soundroid;

import android.app.Application;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import fr.uge.soundroid.database.SoundroidDatabase;
import fr.uge.soundroid.database.dao.PlaylistDao;
import fr.uge.soundroid.database.dao.PlaylistSongsJoinDao;
import fr.uge.soundroid.database.dao.SongDao;
import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.PlaylistSongsJoin;
import fr.uge.soundroid.database.entity.Song;

import static android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM;
import static android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_GENRE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;

public class IndexService {
    private SongDao songDao;
    private PlaylistDao playlistDao;
    private long playlistId;
    private PlaylistSongsJoinDao playlistSongsJoinDao;

    public IndexService(Application app) {
        songDao = SoundroidDatabase.getDatabase(app).songDao();
        playlistDao = SoundroidDatabase.getDatabase(app).playlistDao();
        playlistSongsJoinDao = SoundroidDatabase.getDatabase(app).playlistSongsJoinDao();
    }

    public void addMusicFilesFromRoot() {
        // TODO : stop deleting everything when starting indexation
        playlistSongsJoinDao.deleteAll();
        playlistDao.deleteAll();
        songDao.deleteAll();
        Log.d("musicsList", "Starting indexation");
        playlistId = playlistDao.insert(new Playlist("Musics"));
        //TODO : find a method that isnt deprecated to get root path of external storage
        addMusicFilesFrom(Environment.getExternalStoragePublicDirectory("").getAbsolutePath(), 0);
        Log.d("Song list", songDao.getAll().toString());
    }

    private void addMusicFilesFrom(String dirPath, int index) {
        final File dir = new File(dirPath);
        final File[] files = dir.listFiles();
        assert files != null;
        int i = 0;
        for (File file: files) {
            if (file.isDirectory()) {
                addMusicFilesFrom(file.getAbsolutePath(), index);
            } else if (file.isFile() && file.getPath().endsWith(".mp3")) {
                MediaMetadataRetriever mtr = new MediaMetadataRetriever();
                mtr.setDataSource(file.getAbsolutePath());

                String title = mtr.extractMetadata(METADATA_KEY_TITLE);
                String artist = mtr.extractMetadata(METADATA_KEY_ARTIST);
                String album = mtr.extractMetadata(METADATA_KEY_ALBUM);
                String genre = mtr.extractMetadata(METADATA_KEY_GENRE);
                String duration = mtr.extractMetadata(METADATA_KEY_DURATION);


                title = (title == null) ? "Track " + (++index) : title;
                artist = (artist == null) ? "Unknown artist" : artist;
                album = (album == null) ? "Unknown album" : album;
                genre = (genre == null) ? "Unknown genre" : genre;

                String s = title + artist + album + genre + duration;
                Log.d("currentSong", s);
                final String songHash = String.valueOf(s.hashCode());

                Song song = new Song(title, Long.parseLong(duration), null, artist, album, songHash, file.getAbsolutePath());
                song.songId = songDao.insert(song);
                // TODO : should we always insert the new indexed song into the 'Musics' playlist ? (it is the current behavior)
                playlistSongsJoinDao.insert(new PlaylistSongsJoin(playlistId, song.songId));
            }
        }
    }
}
