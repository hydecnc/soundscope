package org.wavelabs.soundscope.interface_adapter;

import org.wavelabs.soundscope.use_cases.saveRecording.SaveRecordingOB;
import org.wavelabs.soundscope.use_cases.startRecording.StartRecordingOB;
import org.wavelabs.soundscope.use_cases.stopRecording.StopRecordingOB;

public class DummyPresenter implements StartRecordingOB,
                                       StopRecordingOB,
                                       SaveRecordingOB {

    @Override
    public void presentSaveSuccessView() {

    }

    @Override
    public void presentRecordingView() {

    }

    @Override
    public void presentRecordEndView() {

    }
}
