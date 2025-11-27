package org.wavelabs.soundscope.interface_adapter.fingerprint;

import org.wavelabs.soundscope.use_case.fingerprint.FingerprintOB;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintOD;

public class FingerprintPresenter implements FingerprintOB {
    private FingerprintViewModel fingerprintViewModel;

    public FingerprintPresenter(FingerprintViewModel fingerprintViewModel) {
        this.fingerprintViewModel = fingerprintViewModel;
    }

    @Override
    public void prepareSuccessView(FingerprintOD outputData) {
        final FingerprintState fingerprintState = fingerprintViewModel.getState();
        fingerprintState.setFingerprint(outputData.getFingerprint());
        fingerprintViewModel.setState(fingerprintState);
        fingerprintViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {

    }
}
