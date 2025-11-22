package org.wavelabs.soundscope.use_case.fingerprint;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingDAI;

/**
 * Fingerprinter interactor. This takes in the entire audio clip stored in {@code AudioRecording}
 * and processes to generate a string of fingerprint of that audio.
 */
public class FingerprinterInteractor implements FingerprinterIB {

    private final SaveRecordingDAI saveRecordingDAO;

    public FingerprinterInteractor(SaveRecordingDAI saveRecordingDAI) {
        this.saveRecordingDAO = saveRecordingDAI;
    }

    @Override
    public FingerprinterOD execute() {
        final AudioRecording inputData = saveRecordingDAO.getAudioRecording();

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
