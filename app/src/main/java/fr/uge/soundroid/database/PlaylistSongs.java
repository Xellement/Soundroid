package fr.uge.soundroid.database;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

import fr.uge.soundroid.database.entity.Playlist;
import fr.uge.soundroid.database.entity.PlaylistSongsCrossRef;
import fr.uge.soundroid.database.entity.Song;

public class PlaylistSongs {
    @Embedded public Playlist playlist;
    @Relation(
            parentColumn = "playlistId",
            entityColumn = "songId",
            associateBy = @Junction(PlaylistSongsCrossRef.class)
    )
    public List<Song> songs;

}
