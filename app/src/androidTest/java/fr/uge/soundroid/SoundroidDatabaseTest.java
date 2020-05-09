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
import java.util.List;

import fr.uge.soundroid.database.SoundroidDatabase;
import fr.uge.soundroid.database.entity.Album;
import fr.uge.soundroid.database.entity.Artist;
import fr.uge.soundroid.database.entity.Song;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SoundroidDatabaseTest {
    private SoundroidDatabase db;

    private static Artist provideArtist() {
        Artist a = new Artist();
        a.artistName = "Johnny";
        return a;
    }
    private static Album provideAlbum() {
        Album a= new Album();
        a.albumTitle = "Time after time";
        a.nbSongs = 15;
        return a;
    }
    private static Song provideSong() {
        Song song = new Song();
        song.songTitle = "Crying";
        song.songMark = 5;
        song.songTag = "song";
        song.songDuration = 180;
        return song;
    }

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
        Artist artist = provideArtist();
        db.artistDao().insert(artist);

        Artist byName = db.artistDao().findByName("Johnny");
        assertEquals(artist, byName);
    }

    @Test
    public void writeAlbumAndRead() {
        Album album = provideAlbum();
        db.albumDao().insert(album);
        Album byName = db.albumDao().findByName("Time after time");
        assertEquals(album, byName);
    }

    @Test
    public void writeSongAndRead() {
        Artist artist = provideArtist();
        Album album = provideAlbum();
        db.artistDao().insert(artist);
        db.albumDao().insert(album);

        Song song = provideSong();
        song.songAlbumId = db.albumDao().findByName(album.albumTitle).albumId;
        song.songArtistId = db.artistDao().findByName(artist.artistName).artistId;

        db.songDao().insert(song);

        Song byName = db.songDao().findByName("Crying");
        List<Song> byAlbum = db.songDao().findByAlbum("Time after time");
        List<Song> byArtist = db.songDao().findByArtist("Johnny");
        assertEquals(song, byName);
        assertTrue(byAlbum.contains(song));
        assertTrue(byArtist.contains(song));
    }
}
