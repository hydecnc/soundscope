package org.wavelabs.soundscope.interface_adapter.fingerprint;

import org.wavelabs.soundscope.use_case.fingerprint.FingerprintInputBoundary;

public class FingerprintController {
    private final FingerprintInputBoundary fingerprinterUseCaseInteractor;

    public FingerprintController(FingerprintInputBoundary fingerprinterUseCaseInteractor) {
        this.fingerprinterUseCaseInteractor = fingerprinterUseCaseInteractor;
    }

    public void execute() {
        fingerprinterUseCaseInteractor.execute();
    }
}
