package org.wavelabs.soundscope.use_case.stop_recording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.Recorder;

import javax.sound.sampled.AudioFormat;

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
