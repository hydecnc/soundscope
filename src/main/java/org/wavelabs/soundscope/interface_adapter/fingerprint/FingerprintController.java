package org.wavelabs.soundscope.interface_adapter.fingerprint;

import org.wavelabs.soundscope.use_case.fingerprint.FingerprintIB;

public class FingerprintController {
    private final FingerprintIB fingerprinterUseCaseInteractor;

    public FingerprintController(FingerprintIB fingerprinterUseCaseInteractor) {
        this.fingerprinterUseCaseInteractor = fingerprinterUseCaseInteractor;
    }

    public void execute() {
        fingerprinterUseCaseInteractor.execute();
    }
}
