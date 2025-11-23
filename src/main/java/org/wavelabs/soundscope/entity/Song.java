package org.wavelabs.soundscope.entity;

public class Song {

    /**
     * Stores all metadata needed for a song
     * @param title
     * @param duration
     * @param musicBrainzID
     * @param acoustIDTrackID
     * @param album
     * @param artists
     */
    public record SongMetadata(
            String title,
            int duration,
            String musicBrainzID,
            String acoustIDTrackID,
            String album,
            String[] artists
    ){}
}
