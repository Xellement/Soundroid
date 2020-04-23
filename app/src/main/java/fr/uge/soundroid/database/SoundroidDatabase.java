package fr.uge.soundroid.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Song.class}, version=1)
public abstract class SoundroidDatabase extends RoomDatabase {
    public abstract SongDao songDao();
    public abstract ArtistDao artistDao();
}
