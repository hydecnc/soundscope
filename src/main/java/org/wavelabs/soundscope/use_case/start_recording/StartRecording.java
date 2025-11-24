package org.wavelabs.soundscope.use_case.start_recording;

import org.wavelabs.soundscope.infrastructure.Recorder;

public class StartRecording implements StartRecordingIB {
    private final StartRecordingDAI startRecordingDataObject;

    public StartRecording(StartRecordingDAI startRecordingDAI) {
        this.startRecordingDataObject = startRecordingDAI;
    }

    /**
     * execute the actual use case
     */
    @Override
    public void execute(){
        final Recorder recorder = startRecordingDataObject.getRecorder();
        if (!recorder.isRecording())
            recorder.start();
    }
}
