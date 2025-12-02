package org.wavelabs.soundscope.use_case.stop_recording;

import javax.sound.sampled.AudioFormat;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.Recorder;

import javax.sound.sampled.AudioFormat;

/**
 * Use Case Interactor for StopRecording.
 *
 * <p> Contains the application business rules for the StopRecording use case,
 * and implements the StopRecordingIB interface. </p>
 *
 * <p>This interactor terminates the audio recording by:</p>
 * <ul>
 *   <li>Using the StopRecordingDAI to update the recording state.</li>
 *   <li>No Output Data</li>
 * </ul>
 */
public class StopRecording implements StopRecordingIB {
    private final StopRecordingDAI stopRecordingDAO;

    public StopRecording(StopRecordingDAI stopRecordingDAI) {
        this.stopRecordingDAO = stopRecordingDAI;
    }

    @Override
    public void execute() {
        stopRecordingDAO.stopRecording();
    }
}
