package org.wavelabs.soundscope.use_case.identify;

import org.wavelabs.soundscope.entity.Song;

/**
 * Identify interactor. Reads in a recording's fingerprint and duration from Song,
 * then finds the closest matching song from this and retrieves that songs' metadata.
 */
public class IdentifyInteractor implements IdentifyIB {
    private final Song song;
    private final IdentifyOB identifyOutputBoundary;
    private final IdentifyDAI identifier;

    /**
     * Initializes an IdentifyInteractor.
     * @param song the Song object with the fingerprint and duration
     * @param identifyOutputBoundary Handles passing output data
     * @param identifier DAI for the identify use case
     */
    public IdentifyInteractor(Song song, IdentifyOB identifyOutputBoundary, IdentifyDAI identifier) {
        this.song = song;
        this.identifyOutputBoundary = identifyOutputBoundary;
        this.identifier = identifier;
    }

    /**
     * Finds the metadata of the song with metadata closest to that in the stored object.
     */
    @Override
    public void identify() {
        try {
            final Song.SongMetadata metadata =
                identifier.getClosestMatchMetadata(song.getFingerprint(), song.getDuration());
            song.setMetadata(metadata);

            final IdentifyOD outputData = new IdentifyOD(
                    song.getMetadata().title(),
                    song.getMetadata().artists(),
                    song.getMetadata().album()
            );
            identifyOutputBoundary.updateSongAttributes(outputData);
        }
        catch (IdentifyDAI.FingerprintMatchNotFoundException exception) {
            identifyOutputBoundary.presentError(exception.getMessage());
        }
    }
}
