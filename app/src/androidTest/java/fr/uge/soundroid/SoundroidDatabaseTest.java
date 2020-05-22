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
import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.PlaylistSongsJoin;
import fr.uge.soundroid.database.entity.Song;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SoundroidDatabaseTest {
    private SoundroidDatabase db;

    private static Song provideSong() {
        Song song = new Song();
        song.songTitle = "Crying";
        song.liked = true;
        song.songTag = "song";
        song.songDuration = 180;
        song.albumName = "Time after time";
        song.artistName = "Johnny";
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
    public void writeSongAndRead() {
        Song song = provideSong();

        db.songDao().insert(song);

        Song byName = db.songDao().findByName("Crying");
        List<Song> byAlbum = db.songDao().findByAlbum("Time after time");
        List<Song> byArtist = db.songDao().findByArtist("Johnny");
        assertEquals(song, byName);
        assertTrue(byAlbum.contains(song));
        assertTrue(byArtist.contains(song));
    }

    @Test
    public void writeAndDeleteSong() {
        Song song = provideSong();
        assertTrue(db.songDao().getAll().isEmpty());
        song.songId = db.songDao().insert(song);
        Song byName = db.songDao().findByName(song.songTitle);
        assertEquals(byName, song);

        db.songDao().delete(song);
        byName = db.songDao().findByName(song.songTitle);
        assertFalse(db.songDao().getAll().contains(song));
    }

    @Test
    public void createAndGetPlaylist() {
        Song song = provideSong();
        song.songId = db.songDao().insert(song);

        Playlist playlist = new Playlist("playlist1");
        playlist.playlistId = db.playlistDao().insert(playlist);
        PlaylistSongsJoin playlistSongsJoin = new PlaylistSongsJoin(playlist.playlistId, song.songId);
        db.playlistSongsJoinDao().insert(playlistSongsJoin);

        List<Playlist> bySong = db.playlistSongsJoinDao().getPlaylistsFromSong(song.songId);
        assertTrue(bySong.contains(playlist));

        List<Song> byPlaylist = db.playlistSongsJoinDao().getSongsFromPlaylist(playlist.playlistId);
        assertTrue(byPlaylist.contains(song));
    }
}
