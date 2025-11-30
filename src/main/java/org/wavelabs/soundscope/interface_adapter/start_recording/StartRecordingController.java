package org.wavelabs.soundscope.interface_adapter.start_recording;

import org.wavelabs.soundscope.use_case.start_recording.StartRecordingIB;

public class StartRecordingController {
    public final StartRecordingIB startRecordingInteractor;

    public StartRecordingController(StartRecordingIB startRecordingInteractor) {
        this.startRecordingInteractor = startRecordingInteractor;
    }

    public void execute() {
        startRecordingInteractor.execute();
    }
}
