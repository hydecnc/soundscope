package org.wavelabs.soundscope.use_case.identify;

import org.wavelabs.soundscope.entity.Song;

/**
 * DAI for looking up closest match IDs and metadata from a fingerprint
 */
public interface IdentifyDAI {
    /**
     * Returns the closest acoustID track ID associated with a song.
     * @param fingerprint
     * @param duration
     * @return acoustID track ID
     */
    public String getClosestMatchID(String fingerprint, int duration);

    /**
     * Returns metadata associated with a song.
     * @param fingerprint
     * @param duration
     * @return songMetadata
     */
    public Song.SongMetadata getClosestMatchMetadata(String fingerprint, int duration);

    /**
     * Exception type for the fingerprint not having a match
     */
    class FingerprintMatchNotFoundException extends RuntimeException{
        public FingerprintMatchNotFoundException(String fingerprint) {
            super("Fingerprint match could not be found:\n" + fingerprint);
        }
    }
}