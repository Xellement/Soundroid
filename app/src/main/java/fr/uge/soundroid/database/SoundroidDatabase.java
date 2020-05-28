package fr.uge.soundroid.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private static SoundroidDatabase DB_INSTANCE;
    public static final ExecutorService dbExecutor = Executors.newFixedThreadPool(4);

    public static SoundroidDatabase getDatabase(final Context ctx) {
        if (DB_INSTANCE == null) {
            synchronized (SoundroidDatabase.class) {
                if (DB_INSTANCE == null) {
                    DB_INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                        SoundroidDatabase.class, "soundroid_database")
//                            .addCallback(databaseCallback)
                            .build();
                }
            }
        }
        return DB_INSTANCE;
    }

    private static RoomDatabase.Callback databaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    new PopulateDBAsync(DB_INSTANCE).execute();
                }
            };

    private static class PopulateDBAsync extends AsyncTask<Void, Void, Void> {
        private final PlaylistDao playlistDao;

        PopulateDBAsync(SoundroidDatabase db) {
            playlistDao = db.playlistDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // TODO : find how we should pre-populate the db when its created
//            for (int i = 1; i <= 10; i++) {
//                playlistDao.insert(new Playlist("Playlist n°"+i, "playlist_icon.png"));
//            }
//            Log.d("playlistCreation", "Created playlists");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
