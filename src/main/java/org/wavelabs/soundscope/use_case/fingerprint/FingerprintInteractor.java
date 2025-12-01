package org.wavelabs.soundscope.use_case.fingerprint;

import javax.sound.sampled.AudioFormat;

import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.entity.Song;
import org.wavelabs.soundscope.use_case.fingerprint.chromaprint.ChromaprintException;

/**
 * Fingerprint interactor. This takes in the entire audio data and processes to generate a string of
 * fingerprint of that audio.
 */
public class FingerprintInteractor implements FingerprintIB {
    private final FingerprintDAI userDataAccessObject;
    private final FingerprintOB fingerprintPresenter;
    private final Song song;

    public FingerprintInteractor(FingerprintDAI userDataAccessObject, Song song,
                                 FingerprintOB fingerprintOutputBoundary) {
        this.userDataAccessObject = userDataAccessObject;
        this.fingerprintPresenter = fingerprintOutputBoundary;
        this.song = song;
    }

    @Override
    public void execute() {
        try {
            final byte[] bytes = userDataAccessObject.getAudioData();
            final AudioFormat format = userDataAccessObject.getAudioFormat();

            // 120 seconds is the standard AcoustID max

            final int secondsToProcess = 120;
            int bytesPerSample = format.getSampleSizeInBits() / 8;
            final int frameSize = format.getChannels() * bytesPerSample;
            final int maxBytes = (int) (secondsToProcess * format.getSampleRate() * frameSize);

            final int bytesLengthToProcess = Math.min(bytes.length, maxBytes);

            final Fingerprinter fingerprinter =
                new Fingerprinter((int) format.getSampleRate(), format.getChannels());
            fingerprinter.start();
            fingerprinter.processChunk(bytes, bytesLengthToProcess);
            fingerprinter.stop();

            final FingerprintOD output = new FingerprintOD(fingerprinter.getFingerprint());
            fingerprinter.close();

            song.setFingerprint(output.getFingerprint());

            final AudioData buffer = userDataAccessObject.getCurrentRecordingBuffer();
            if (buffer != null) {
                song.setDuration((int) buffer.getDurationSeconds());
            }
            else if (bytes != null && bytes.length > 0) {
                final int sampleRate = (int) format.getSampleRate();
                final int channels = format.getChannels();
                bytesPerSample = format.getSampleSizeInBits() / 8;
                final double durationSeconds =
                    (double) bytes.length / (sampleRate * channels * bytesPerSample);
                song.setDuration((int) durationSeconds);
            }

            fingerprintPresenter.prepareSuccessView(output);
        }

        catch (NullPointerException exception) {
            fingerprintPresenter.prepareFailView(
                "Audio data could not be found. Please record or load an audio file first.");
        }

        catch (ChromaprintException exception) {
            fingerprintPresenter.prepareFailView("Chromaprint error:\n" + exception.getMessage());
        }
    }
}
