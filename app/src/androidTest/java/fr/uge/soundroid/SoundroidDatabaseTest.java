package fr.uge.soundroid;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import fr.uge.soundroid.database.SoundroidDatabase;
import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.PlaylistSongsJoin;
import fr.uge.soundroid.database.entity.Song;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = Room.inMemoryDatabaseBuilder(ctx, SoundroidDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        db.close();
    }


    @Test
    public void writeSongAndRead() throws InterruptedException {
        Song song = provideSong();

        db.songDao().insert(song);

        Song byName = LiveDataTestUtil.getValue(db.songDao().findByName(song.songTitle));
        List<Song> byAlbum = LiveDataTestUtil.getValue(db.songDao().findByAlbum(song.albumName));
        List<Song> byArtist = LiveDataTestUtil.getValue(db.songDao().findByArtist("Johnny"));
        assertEquals(song, byName);
        assertTrue(byAlbum.contains(song));
        assertTrue(byArtist.contains(song));
    }

    @Test
    public void writeAndDeleteSong() throws InterruptedException {
        Song song = provideSong();
        assertTrue(LiveDataTestUtil.getValue(db.songDao().getAll()).isEmpty());
        song.songId = db.songDao().insert(song);
        Song byName = LiveDataTestUtil.getValue(db.songDao().findByName(song.songTitle));
        assertEquals(byName, song);

        db.songDao().delete(song);
        byName = LiveDataTestUtil.getValue(db.songDao().findByName(song.songTitle));
        assertNull(byName);
        assertFalse(LiveDataTestUtil.getValue(db.songDao().getAll()).contains(song));
    }

    @Test
    public void createSongAndLikeIt() throws InterruptedException {
        Song song = new Song("Crying", 120, "music", "Johnny", "Time after Time", null, null);
        song.songId = db.songDao().insert(song);

        assertFalse(song.liked);
        Song byName = LiveDataTestUtil.getValue(db.songDao().findByName("Crying"));
        assertFalse(byName.liked);
        byName.liked = true;
        db.songDao().update(byName);
        assertTrue(LiveDataTestUtil.getValue(db.songDao().findById(byName.songId)).liked);
    }

    @Test
    public void createAndGetPlaylist() throws InterruptedException {
        Song song = new Song("Crying", 120, "music", "Johnny", "Time after Time", null, null);
        song.songId = db.songDao().insert(song);

        Song song2 = new Song("Wait", 150, "mucis", "Lennon", "Imagination", null, null);
        song2.songId = db.songDao().insert(song2);

        Playlist playlist = new Playlist("playlist1");
        playlist.playlistId = db.playlistDao().insert(playlist);
        PlaylistSongsJoin playlistSongsJoin = new PlaylistSongsJoin(playlist.playlistId, song.songId);
        db.playlistSongsJoinDao().insert(playlistSongsJoin);
        playlistSongsJoin = new PlaylistSongsJoin(playlist.playlistId, song2.songId);
        db.playlistSongsJoinDao().insert(playlistSongsJoin);

        List<Playlist> bySong = LiveDataTestUtil.getValue(db.playlistSongsJoinDao().getPlaylistsFromSong(song.songId));
        assertTrue(bySong.size() == 1 && bySong.contains(playlist));

        List<Song> byPlaylist = LiveDataTestUtil.getValue(db.playlistSongsJoinDao().getSongsFromPlaylist(playlist.playlistId));
        assertEquals(2, byPlaylist.size());
        assertTrue(byPlaylist.contains(song));
        assertTrue(byPlaylist.contains(song2));
    }

    @Test
    public void getArtistsAndAlbumsName() throws InterruptedException {
        Song song1 = new Song("Crying", 120, "music", "Johnny", "Time after Time", null, null);
        song1.songId = db.songDao().insert(song1);

        Song song2 = new Song("Wait", 150, "mucis", "Lennon", "Imagination", null, null);
        song2.songId = db.songDao().insert(song2);

        Song song3 = new Song("Hello", 90, "tag", "Unknown artist", "Unknown album", null, null);
        song3.songId = db.songDao().insert(song3);

        Song song4 = new Song("Yo", 110, "tag", "Unknown artist", "Unknown album", null, null);
        song4.songId = db.songDao().insert(song4);

        List<String> artists = LiveDataTestUtil.getValue(db.songDao().getArtistsName());
        List<String> albums = LiveDataTestUtil.getValue(db.songDao().getAlbumsName());
        List<String> artistsExpected = new ArrayList<>(3);
        List<String> albumsExpected = new ArrayList<>(3);

        artistsExpected.add("Johnny");
        artistsExpected.add("Lennon");
        artistsExpected.add("Unknown artist");

        albumsExpected.add("Time after Time");
        albumsExpected.add("Imagination");
        albumsExpected.add("Unknown album");

        assertEquals(artistsExpected, artists);
        assertEquals(albumsExpected, albums);
    }
}
