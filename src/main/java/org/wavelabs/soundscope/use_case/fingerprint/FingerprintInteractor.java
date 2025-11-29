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
            
            if (bytes == null || bytes.length == 0) {
                fingerprintPresenter.prepareFailView("Audio data could not be found. Please record or load an audio file first.");
                return;
            }

            final Fingerprinter fingerprinter = new Fingerprinter();
            fingerprinter.start();
            fingerprinter.processChunk(bytes, bytes.length);
            fingerprinter.stop();

            final FingerprintOD output = new FingerprintOD(fingerprinter.getFingerprint());
            fingerprinter.close();

            song.setFingerprint(output.getFingerprint());
            
            org.wavelabs.soundscope.entity.AudioData buffer = userDataAccessObject.getCurrentRecordingBuffer();
            if (buffer != null) {
                song.setDuration((int) buffer.getDurationSeconds());
            } else if (bytes != null && bytes.length > 0) {
                int sampleRate = 44100;
                int channels = 2;
                int bytesPerSample = 2;
                double durationSeconds = (double) bytes.length / (sampleRate * channels * bytesPerSample);
                song.setDuration((int) durationSeconds);
            }
            
            fingerprintPresenter.prepareSuccessView(output);
        } catch (NullPointerException e){
            fingerprintPresenter.prepareFailView("Audio data could not be found. Please record or load an audio file first.");
        } catch (ChromaprintException e){
            fingerprintPresenter.prepareFailView("Chromaprint error:\n" + e.getMessage());
        }
    }
}
