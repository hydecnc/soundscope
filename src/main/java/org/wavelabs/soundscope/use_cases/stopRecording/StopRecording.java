package org.wavelabs.soundscope.use_cases.stopRecording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.Recorder;

import javax.sound.sampled.AudioFormat;

public class StopRecording implements StopRecordingIB {
    private final StopRecordingDAI stopRecordingDAO;
    private final StopRecordingOB stopRecordingPresenter;

    public StopRecording(StopRecordingDAI stopRecordingDAI, StopRecordingOB stopRecordingOB) {
        this.stopRecordingDAO = stopRecordingDAI;
        this.stopRecordingPresenter = stopRecordingOB;
    }

    @Override
    public void execute() {
        final Recorder recorder = stopRecordingDAO.getRecorder();
        final AudioFormat format = recorder.getAudioFormat();

        recorder.stop();

        // extract and save the resulting byte[] to audioRecording object
        AudioRecording audioRecording = new AudioRecording(recorder.getRecordingBytes(), format);
        // toss the object to the DAO
        stopRecordingDAO.setAudioRecording(audioRecording);
        stopRecordingPresenter.presentRecordEndView();
    }
}
