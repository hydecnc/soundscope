package org.wavelabs.soundscope.interface_adapter;

import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingOB;
import org.wavelabs.soundscope.use_case.start_recording.StartRecordingOB;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecordingOB;

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
