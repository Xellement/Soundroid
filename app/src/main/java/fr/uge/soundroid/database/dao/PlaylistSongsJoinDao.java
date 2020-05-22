package fr.uge.soundroid.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.PlaylistSongsJoin;
import fr.uge.soundroid.database.entity.Song;

@Dao
public interface PlaylistSongsJoinDao {
    @Insert
    long insert(PlaylistSongsJoin playlistSongsJoin);

    @Delete
    void delete(PlaylistSongsJoin playlistSongsJoin);

    @Update
    void update(PlaylistSongsJoin playlistSongsJoin);

    @Query("SELECT * FROM song " +
            "INNER JOIN playlist_songs_join ON song.songId = playlist_songs_join.songId " +
            "WHERE playlist_songs_join.playlistId = :playlistId")
    LiveData<List<Song>> getSongsFromPlaylist(long playlistId);

    @Query("SELECT * FROM playlist " +
            "INNER JOIN playlist_songs_join ON playlist.playlistId = playlist_songs_join.playlistId " +
            "WHERE playlist_songs_join.songId = :songId")
    LiveData<List<Playlist>> getPlaylistsFromSong(long songId);
}
