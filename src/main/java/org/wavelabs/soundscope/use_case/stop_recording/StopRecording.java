package org.wavelabs.soundscope.use_case.stop_recording;

import javax.sound.sampled.AudioFormat;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.Recorder;

public class StopRecording implements StopRecordingIB {
    private final StopRecordingDAI stopRecordingDAO;

    public StopRecording(StopRecordingDAI stopRecordingDAI) {
        this.stopRecordingDAO = stopRecordingDAI;
    }

    @Override
    public void execute() {
        final Recorder recorder = stopRecordingDAO.getRecorder();
        final AudioFormat format = recorder.getAudioFormat();

        recorder.stop();

        // extract and save the resulting byte[] to audioRecording object
        final AudioRecording audioRecording = new AudioRecording(recorder.getRecordingBytes(), format);
        // toss the object to the DAO
        stopRecordingDAO.setAudioRecording(audioRecording);
    }
}
