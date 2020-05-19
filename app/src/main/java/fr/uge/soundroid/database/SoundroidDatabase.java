package fr.uge.soundroid.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import fr.uge.soundroid.database.dao.AlbumDao;
import fr.uge.soundroid.database.dao.ArtistDao;
import fr.uge.soundroid.database.dao.PlaylistSongsDao;
import fr.uge.soundroid.database.dao.SongDao;
import fr.uge.soundroid.database.entity.Album;
import fr.uge.soundroid.database.entity.Artist;
import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.PlaylistSongsCrossRef;
import fr.uge.soundroid.database.entity.Song;

@Database(entities = {Song.class, Artist.class, Album.class, Playlist.class, PlaylistSongsCrossRef.class}, version=1)
public abstract class SoundroidDatabase extends RoomDatabase {
    public abstract SongDao songDao();
    public abstract ArtistDao artistDao();
    public abstract AlbumDao albumDao();
    public abstract PlaylistSongsDao playlistDao();

}
