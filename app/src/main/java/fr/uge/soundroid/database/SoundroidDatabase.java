package fr.uge.soundroid.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import fr.uge.soundroid.database.dao.PlaylistDao;
import fr.uge.soundroid.database.dao.PlaylistSongsJoinDao;
import fr.uge.soundroid.database.dao.SongDao;
import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.PlaylistSongsJoin;
import fr.uge.soundroid.database.entity.Song;

@Database(entities = {Song.class, Playlist.class, PlaylistSongsJoin.class}, version=1)
public abstract class SoundroidDatabase extends RoomDatabase {
    public abstract SongDao songDao();
    public abstract PlaylistDao playlistDao();
    public abstract PlaylistSongsJoinDao playlistSongsJoinDao();

    private static SoundroidDatabase db;

    public static SoundroidDatabase getDatabase(final Context ctx) {
        if (db == null) {
            synchronized (SoundroidDatabase.class) {
                if (db == null) {
                    db = Room.databaseBuilder(ctx.getApplicationContext(),
                        SoundroidDatabase.class, "soundroid_database").build();
                }
            }
        }
        return db;
    }
}
