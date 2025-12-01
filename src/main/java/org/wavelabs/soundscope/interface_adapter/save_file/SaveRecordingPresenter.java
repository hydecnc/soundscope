package org.wavelabs.soundscope.interface_adapter.save_file;

import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingOB;

/**
 * Presenter class for save_recording use case.
 * Stores the result in state after use case execution.
 */
public class SaveRecordingPresenter implements SaveRecordingOB {
    private final MainViewModel mainViewModel;

    public SaveRecordingPresenter(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    @Override
    public void presentSaveSuccessView() {
    }

    @Override
    public void presentError(String message) {
        mainViewModel.getState().setErrorState(true);
        mainViewModel.getState().setErrorMessage(message);
        mainViewModel.firePropertyChange("file save");
    }
}
