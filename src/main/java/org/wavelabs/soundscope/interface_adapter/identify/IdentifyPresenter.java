package org.wavelabs.soundscope.interface_adapter.identify;

import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.use_case.identify.IdentifyOB;
import org.wavelabs.soundscope.use_case.identify.IdentifyOD;

public class IdentifyPresenter implements IdentifyOB {
    private final MainViewModel mainViewModel;

    public IdentifyPresenter(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    @Override
    public void updateSongAttributes(IdentifyOD outputData) {
        mainViewModel.getState().setSongTitle(outputData.songTitle());
        mainViewModel.getState().setAlbum(outputData.album());
        String artists = String.join(", ",  outputData.artists());
        mainViewModel.getState().setArtists(artists);
        mainViewModel.firePropertyChange("identify");
    }

    @Override
    public void presentError(String errorMessage) {
        mainViewModel.getState().setErrorState(true);
        mainViewModel.getState().setErrorMessage(errorMessage);
        mainViewModel.firePropertyChange("identify");
    }
}
