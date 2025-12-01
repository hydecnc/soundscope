package org.wavelabs.soundscope.entity;

public class Song {
    private SongMetadata metadata;
    private int duration;
    private String fingerprint;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public SongMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SongMetadata metadata) {
        this.metadata = metadata;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    /**
     * Stores all metadata needed for a song.
     *
     * @param title title of song
     * @param musicBrainzID id for musicBrainz (I'm guessing)
     * @param acoustIDTrackID acoustID track id for API
     * @param album album of song
     * @param artists artists of song
     */
    public record SongMetadata(
        String title,
        String musicBrainzID,
        String acoustIDTrackID,
        String album,
        String[] artists
    ) {
    }
}
