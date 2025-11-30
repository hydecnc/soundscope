package org.wavelabs.soundscope.interface_adapter.stop_recording;

import org.wavelabs.soundscope.use_case.stop_recording.StopRecordingIB;

public class StopRecordingController {
    private final StopRecordingIB stopRecordingInteractor;

    public StopRecordingController(StopRecordingIB stopRecordingInteractor) {
        this.stopRecordingInteractor = stopRecordingInteractor;
    }

    public void execute() {
        stopRecordingInteractor.execute();
    }
}
