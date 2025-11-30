package org.wavelabs.soundscope.use_case.identify;

import org.wavelabs.soundscope.data_access.AcousticIDIdentify;
import org.wavelabs.soundscope.entity.Song;

public class IdentifyInteractor implements IdentifyIB {
    private final Song song;
    private final IdentifyOB identifyOutputBoundary;

    public IdentifyInteractor(Song song, IdentifyOB identifyOutputBoundary) {
        this.song = song;
        this.identifyOutputBoundary = identifyOutputBoundary;
    }

    @Override
    public void identify() {
        final IdentifyDAI identifier = new AcousticIDIdentify();

        try {
            final Song.SongMetadata metadata =
                identifier.getClosestMatchMetadata(song.getFingerprint(), song.getDuration());
            song.setMetadata(metadata);

            IdentifyOD outputData = new IdentifyOD(song.getMetadata().title(), song.getMetadata().album());
            identifyOutputBoundary.updateSongAttributes(outputData);
        } catch (IdentifyDAI.FingerprintMatchNotFoundException e) {
            identifyOutputBoundary.presentError(e.getMessage());
        }
    }
}
