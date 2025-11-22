package org.wavelabs.soundscope.use_case.fingerprint;

import org.wavelabs.soundscope.data_access.FileDAO;
import org.wavelabs.soundscope.entity.AudioRecording;

/**
 * Fingerprinter interactor. This takes in the entire audio clip stored in {@code AudioRecording}
 * and processes to generate a string of fingerprint of that audio.
 */
public class FingerprinterInteractor implements FingerprinterIB {

    private final FileDAO fileDAO;

    public FingerprinterInteractor(FileDAO saveRecordingDAI) {
        this.fileDAO = saveRecordingDAI;
    }

    @Override
    public FingerprinterOD execute() {
        final AudioRecording inputData = fileDAO.getAudioRecording();

        final byte[] audioData = inputData.getData();

        Fingerprinter fingerprinter = new Fingerprinter();
        fingerprinter.start();
        fingerprinter.processChunk(audioData, audioData.length);
        fingerprinter.stop();

        FingerprinterOD output = new FingerprinterOD(fingerprinter.getFingerprint());
        fingerprinter.close();
        return output;
    }
}
