package org.wavelabs.soundscope.interface_adapter.play_recording;

import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingOB;

public class PlayRecordingPresenter implements PlayRecordingOB {
    private MainViewModel mainViewModel;

    public PlayRecordingPresenter(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }


    //TODO: finish these methods
    @Override
    public void playbackStarted() {

    }

    @Override
    public void playbackPaused() {

    }

    @Override
    public void playbackStopped() {

    }

    @Override
    public void playbackError(String message) {
        //TODO: set some kind of error state
        mainViewModel.getState().setErrorMessage(message);
        mainViewModel.firePropertyChange("Playing");
    }
}
