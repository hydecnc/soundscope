package org.wavelabs.soundscope.interface_adapter.start_recording;

import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.use_case.start_recording.RecordingOB;
import org.wavelabs.soundscope.use_case.start_recording.RecordingOD;

public class RecordingPresenter implements RecordingOB {
    public MainViewModel mainViewModel;

    public RecordingPresenter(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    @Override
    public void updateRecordingState(RecordingOD outputData) {
        mainViewModel.getState().setRecording(outputData.isPlaying());
        mainViewModel.firePropertyChange("recording");
    }
}
