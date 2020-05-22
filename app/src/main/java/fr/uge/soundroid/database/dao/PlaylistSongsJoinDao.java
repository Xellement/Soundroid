package fr.uge.soundroid.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.PlaylistSongsCrossRef;
import fr.uge.soundroid.database.entity.Song;

@Dao
public interface PlaylistSongsDao {
//    @Transaction
//    @Query("SELECT * FROM playlist")
//    List<PlaylistSongs> getPlaylistWithSongs();

    @Insert
    void insert(PlaylistSongsCrossRef playlistSongsCrossRef);

    @Query("SELECT * FROM song " +
            "INNER JOIN playlist_songs_crossref ON song.songId = playlist_songs_crossref.songId " +
            "WHERE playlist_songs_crossref.playlistId = :playlistId")
    List<Song> getSongsFromPlaylist(int playlistId);

    @Query("SELECT * FROM playlist " +
            "INNER JOIN playlist_songs_crossref ON playlist.playlistId = playlist_songs_crossref.playlistId " +
            "WHERE playlist_songs_crossref.songId = :songId")
    List<Playlist> getPlaylistsFromSong(int songId);
}
