package org.wavelabs.soundscope.entity;

public class Song { //TODO: javadoc
    private SongMetadata metadata;
    private String fingerprint;

    public void setFingerprint(String fingerprint){
        this.fingerprint = fingerprint;
    }

    public void setMetadata(SongMetadata metadata){
        this.metadata = metadata;
    }

    public SongMetadata getMetadata() {
        return metadata;
    }

    public  String getFingerprint() {
        return fingerprint;
    }


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
