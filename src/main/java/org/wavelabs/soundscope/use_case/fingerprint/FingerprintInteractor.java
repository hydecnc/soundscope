package org.wavelabs.soundscope.use_case.fingerprint;

import org.wavelabs.soundscope.entity.Song;
import org.wavelabs.soundscope.use_case.fingerprint.chromaprint.ChromaprintException;

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
        try {
            final byte[] bytes = userDataAccessObject.getAudioData();

            final Fingerprinter fingerprinter = new Fingerprinter();
            fingerprinter.start();
            fingerprinter.processChunk(bytes, bytes.length);
            fingerprinter.stop();

            final FingerprintOD output = new FingerprintOD(fingerprinter.getFingerprint());
            fingerprinter.close();

            song.setFingerprint(output.getFingerprint()); //TODO: is this the right place to put song metadata updating?
            song.setDuration((int) userDataAccessObject.getCurrentRecordingBuffer().getDurationSeconds());
            fingerprintPresenter.prepareSuccessView(output);
        } catch (NullPointerException e){
            fingerprintPresenter.prepareFailView("Audio data could not be found");
        } catch (ChromaprintException e){
            fingerprintPresenter.prepareFailView("Chromaprint error:\n" + e.getMessage());
        }
    }
}
