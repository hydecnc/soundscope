package org.wavelabs.soundscope.use_case.fingerprint;

import org.wavelabs.soundscope.entity.Song;

/**
 * Fingerprint interactor. This takes in the entire audio data and
 * processes to generate a string of fingerprint of that audio.
 */
public class FingerprintInteractor implements FingerprintIB {
    private final FingerprintDAI userDataAccessObject;
    private final FingerprintOB fingerprintPresenter;
    private final Song song;

    public FingerprintInteractor(FingerprintDAI userDataAccessObject, Song song,  FingerprintOB fingerprintOutputBoundary) {
        this.userDataAccessObject = userDataAccessObject;
        this.fingerprintPresenter = fingerprintOutputBoundary;
        this.song = song;
    }

    @Override
    public void execute() {
        final byte[] bytes = userDataAccessObject.getAudioData();

        final Fingerprinter fingerprinter = new Fingerprinter();
        fingerprinter.start();
        fingerprinter.processChunk(bytes, bytes.length);
        fingerprinter.stop();

        final FingerprintOD output = new FingerprintOD(fingerprinter.getFingerprint());
        fingerprinter.close();

        fingerprintPresenter.prepareSuccessView(output);
    }
}
