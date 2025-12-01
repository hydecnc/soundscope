package org.wavelabs.soundscope.interface_adapter.play_recording;

import org.wavelabs.soundscope.interface_adapter.MainState;
import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingOB;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingOD;

public class PlayRecordingPresenter implements PlayRecordingOB {
    public static final String PLAYING = "playing";
    private final MainViewModel mainViewModel;

    public PlayRecordingPresenter(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    @Override
    public void playbackStarted() {
        mainViewModel.getState().setPlaying(true);
        mainViewModel.firePropertyChange(PLAYING);
    }

    @Override
    public void playbackPaused() {
        mainViewModel.getState().setPlaying(false);
        mainViewModel.firePropertyChange(PLAYING);
    }

    @Override
    public void playbackStopped() {
        mainViewModel.getState().setPlaying(false);
        mainViewModel.firePropertyChange(PLAYING);
    }

    @Override
    public void playbackError(String message) {
        mainViewModel.getState().setErrorState(true);
        mainViewModel.getState().setErrorMessage(message);
        mainViewModel.firePropertyChange(PLAYING);
    }

    @Override
    public void updateMainState(PlayRecordingOD updateData) {
        final MainState state = mainViewModel.getState();
        state.setPlaying(updateData.isPlaying());
        state.setFramesPlayed(updateData.framesPlayed());
        state.setPlayingFinished(updateData.playingFinished());
        mainViewModel.firePropertyChange(PLAYING);
    }
}
