package fr.uge.soundroid.service;

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
import static android.media.MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_GENRE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;

public class IndexService {
    private SongDao songDao;
    private PlaylistDao playlistDao;
    private long musicsPlaylist;
    private long index;
    private PlaylistSongsJoinDao playlistSongsJoinDao;

    public IndexService(Application app) {
        songDao = SoundroidDatabase.getDatabase(app).songDao();
        playlistDao = SoundroidDatabase.getDatabase(app).playlistDao();
        playlistSongsJoinDao = SoundroidDatabase.getDatabase(app).playlistSongsJoinDao();
    }

    public void addMusicFilesFromRoot() {
        // TODO : stop deleting everything when starting indexation
//        playlistSongsJoinDao.deleteAll();
//        playlistDao.deleteAll();
//        songDao.deleteAll();
        Log.d("musicsList", "Starting indexation");
        musicsPlaylist = playlistDao.insert(new Playlist("Musics", 0));
        //TODO : find a method that isnt deprecated to get root path of external storage
        addMusicFilesFrom(Environment.getExternalStoragePublicDirectory("").getAbsolutePath());
        Log.d("Song list", songDao.getAll().toString());
    }

    private void addMusicFilesFrom(String dirPath) {
        final File dir = new File(dirPath);
        final File[] files = dir.listFiles();
        assert files != null;
        for (File file: files) {
            if (file.isDirectory()) {
                addMusicFilesFrom(file.getAbsolutePath());
            } else if (file.isFile() && file.getPath().endsWith(".mp3")) {
                MediaMetadataRetriever mtr = new MediaMetadataRetriever();
                mtr.setDataSource(file.getAbsolutePath());

                String title = mtr.extractMetadata(METADATA_KEY_TITLE);
                String artist = mtr.extractMetadata(METADATA_KEY_ARTIST);
                String album = mtr.extractMetadata(METADATA_KEY_ALBUM);
                String genre = mtr.extractMetadata(METADATA_KEY_GENRE);
                String duration = mtr.extractMetadata(METADATA_KEY_DURATION);
                String songNumber = mtr.extractMetadata(METADATA_KEY_CD_TRACK_NUMBER);
                // TODO : should we index songs that doesn't have metadata ?
                if (title == null) continue;

                title = (title == null) ? "Track " + (++index) : title;
                artist = (artist == null) ? "Unknown artist" : artist;
                album = (album == null) ? "Unknown album" : album;
                genre = (genre == null) ? "Unknown genre" : genre;
                int songNumberInt = (songNumber == null) ? 0 : Integer.parseInt(songNumber);
                byte[] picture = mtr.getEmbeddedPicture();

                String s = title + artist + album + genre + duration;
                final String songHash = String.valueOf(s.hashCode());

                Song song = new Song(title, Long.parseLong(duration), null, artist, album
                        , songHash, file.getAbsolutePath(), picture, songNumberInt);
                insertSong(song);
                mtr.release();
            }
        }
    }

    private void insertSong(Song song) {
        long id = songDao.insert(song);
        Log.d("songId", "id : " + id);
        if (id < 0) {
            return;
        }
        else {
            song.songId = id;
        }
        long artistsPlaylistId = playlistDao.insert(new Playlist(song.artistName, "artist_icon.png", 1, song.artistName));
        if (artistsPlaylistId <= 0) {
            artistsPlaylistId = playlistDao.getIdByName(song.artistName);
        }
        long albumsPlaylistId = playlistDao.insert(new Playlist(song.albumName, "album_icon.png", 2, song.artistName));
        if (albumsPlaylistId == -1) {
            albumsPlaylistId = playlistDao.getIdByName(song.albumName);
        }
        if (musicsPlaylist == -1) {
            musicsPlaylist = playlistDao.getIdByName("Musics");
        }
        playlistSongsJoinDao.insert(new PlaylistSongsJoin(artistsPlaylistId, song.songId));
        playlistSongsJoinDao.insert(new PlaylistSongsJoin(albumsPlaylistId, song.songId));
        playlistSongsJoinDao.insert(new PlaylistSongsJoin(musicsPlaylist, song.songId));
    }
}
