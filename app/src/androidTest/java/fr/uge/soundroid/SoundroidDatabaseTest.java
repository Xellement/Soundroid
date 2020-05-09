package fr.uge.soundroid;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import fr.uge.soundroid.database.Artist;
import fr.uge.soundroid.database.SoundroidDatabase;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class SoundroidDatabaseTest {
    private SoundroidDatabase db;

    @Before
    public void createDb() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = Room.inMemoryDatabaseBuilder(ctx, SoundroidDatabase.class).build();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeArtistAndRead() throws IOException {
        Artist artist = new Artist();
        artist.artistName = "Johnny";
        db.artistDao().insert(artist);

        Artist byName = db.artistDao().findByName("Johnny");
        assertEquals("Johnny", byName.artistName);
    }
}
