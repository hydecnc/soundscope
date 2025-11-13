package org.wavelabs.soundscope.use_cases.startRecording;

import org.wavelabs.soundscope.infrastructure.Recorder;

public class StartRecording implements StartRecordingIB {
    private final StartRecordingDAI startRecordingDataObject;
    private final StartRecordingOB startRecordingPresenter;

    public StartRecording(StartRecordingDAI startRecordingDAI, StartRecordingOB startRecordingPresenter) {
        this.startRecordingDataObject = startRecordingDAI;
        this.startRecordingPresenter = startRecordingPresenter;
    }

    /**
     * execute the actual use case
     */
    @Override
    public void execute(){
        final Recorder recorder = startRecordingDataObject.getRecorder();
        if (!recorder.isRecording())
            recorder.start();

        startRecordingPresenter.presentRecordingView();
    }
}
