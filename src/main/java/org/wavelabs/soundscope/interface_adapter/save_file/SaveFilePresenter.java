package org.wavelabs.soundscope.interface_adapter.save_file;

import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingOB;


/**
 * Presenter class for save_recording use case.
 * Stores the result in state after use case execution.
 */
public class SaveFilePresenter implements SaveRecordingOB {
    private final SaveFileState state;

    public SaveFilePresenter(SaveFileState saveFileState) {
        this.state = saveFileState;
    }

    @Override
    public void presentSaveSuccessView() {
        state.setSuccess(true);
    }

    @Override
    public void presentError(String message) {
        state.setError(message);
    }
}
