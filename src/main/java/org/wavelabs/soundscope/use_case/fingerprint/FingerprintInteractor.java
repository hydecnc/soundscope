package org.wavelabs.soundscope.use_case.fingerprint;

/**
 * Fingerprint interactor. This takes in the entire audio data and
 * processes to generate a string of fingerprint of that audio.
 */
public class FingerprintInteractor implements FingerprintInputBoundary {
    private final FingerprintDAI userDataAccessObject;
    private final FingerprintOB fingerprintPresenter;

    public FingerprintInteractor(FingerprintDAI userDataAccessObject, FingerprintOB fingerprintOutputBoundary) {
        this.userDataAccessObject = userDataAccessObject;
        this.fingerprintPresenter = fingerprintOutputBoundary;
    }

    @Override
    public void execute() {
        final byte[] bytes = userDataAccessObject.getAudioData();

        final Fingerprinter fingerprinter = new Fingerprinter();
        fingerprinter.start();
        fingerprinter.processChunk(bytes, bytes.length);
        fingerprinter.stop();

        final FingerprintOD output = new FingerprintOD(fingerprinter.getFingerprint());
        fingerprinter.close();

        fingerprintPresenter.prepareSuccessView(output);
    }
}
