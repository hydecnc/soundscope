package org.wavelabs.soundscope.interface_adapter.fingerprint;

import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintOB;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintOD;

public class FingerprintPresenter implements FingerprintOB {
    private final MainViewModel viewModel;

    public FingerprintPresenter(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(FingerprintOD outputData) {
        viewModel.getState().setFingerprint(outputData.getFingerprint());
        viewModel.firePropertyChange("fingerprint");
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.getState().setErrorState(true);
        viewModel.getState().setErrorMessage(errorMessage);
        viewModel.firePropertyChange("fingerprint");
    }
}
