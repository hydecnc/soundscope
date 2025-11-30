package org.wavelabs.soundscope.interface_adapter.play_recording;

import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingOB;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingOD;
import org.wavelabs.soundscope.interface_adapter.MainState;

public class PlayRecordingPresenter implements PlayRecordingOB {
    private final MainViewModel mainViewModel;

    public PlayRecordingPresenter(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    @Override
    public void playbackStarted() {
        mainViewModel.getState().setPlaying(true);
        mainViewModel.firePropertyChange("playing");
    }

    @Override
    public void playbackPaused() {
        mainViewModel.getState().setPlaying(false);
        mainViewModel.firePropertyChange("playing");
    }

    @Override
    public void playbackStopped() {
        mainViewModel.getState().setPlaying(false);
        mainViewModel.firePropertyChange("playing");
    }

    @Override
    public void playbackError(String message) {
        mainViewModel.getState().setErrorState(true);
        mainViewModel.getState().setErrorMessage(message);
        mainViewModel.firePropertyChange("playing");
    }

    @Override
    public void updateMainState(PlayRecordingOD updateData) {
        MainState state = mainViewModel.getState();
        state.setPlaying(updateData.isPlaying());
        state.setFramesPlayed(updateData.framesPlayed());
        state.setPlayingFinished(updateData.playingFinished());
        mainViewModel.firePropertyChange("playing");
    }


}
