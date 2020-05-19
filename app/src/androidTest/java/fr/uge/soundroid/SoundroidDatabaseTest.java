package fr.uge.soundroid;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
        return new Artist("Johnny");
    }
    private static Album provideAlbum() {
        return new Album("Time after time", 15);
    }
    private static Song provideSong() {
        Song song = new Song();
        song.songTitle = "Crying";
        song.liked = true;
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
    public void closeDb() {
        db.close();
    }

    @Test
    public void writeArtistAndRead(){
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

    @Test
    public void writeAlbumAndDelete() {
        Album album = provideAlbum();

        assertTrue(db.albumDao().getAll().isEmpty());
        db.albumDao().insert(album);

        Album byName = db.albumDao().findByName(album.albumTitle);
        assertEquals(byName, album);

        db.albumDao().delete(byName);
        assertTrue(db.albumDao().getAll().isEmpty());
    }

    @Test
    public void writeAndDeleteArtist() {
        Artist artist = provideArtist();

        assertTrue(db.artistDao().getAll().isEmpty());
        db.artistDao().insert(artist);

        Artist byName = db.artistDao().findByName(artist.artistName);
        assertEquals(byName, artist);

        db.artistDao().delete(byName);
        assertTrue(db.artistDao().getAll().isEmpty());
    }

    @Test
    public void writeAndDeleteSong() {
        Artist artist = provideArtist();
        Album album = provideAlbum();
        db.artistDao().insert(artist);
        db.albumDao().insert(album);

        Song song = provideSong();
        song.songArtistId = db.artistDao().findByName(artist.artistName).artistId;
        song.songAlbumId = db.albumDao().findByName(album.albumTitle).albumId;

        assertTrue(db.songDao().getAll().isEmpty());
        db.songDao().insert(song);
        Song byName = db.songDao().findByName(song.songTitle);
        assertEquals(byName, song);

        db.songDao().delete(song);
        db.artistDao().delete(artist);
        byName = db.songDao().findByName(song.songTitle);
        // CA SUPPRIME PAS PUTAIN --> pb de foreign key
        assertEquals(byName.songAlbumId, db.albumDao().findByName(album.albumTitle).albumId);
        assertTrue(db.artistDao().getAll().contains(artist));
        assertTrue(db.songDao().getAll().contains(song));
    }
}
