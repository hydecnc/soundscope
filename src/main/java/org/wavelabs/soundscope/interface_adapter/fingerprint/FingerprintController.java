package org.wavelabs.soundscope.interface_adapter.fingerprint;

import org.wavelabs.soundscope.use_case.fingerprint.FingerprintIB;

/**
 * Controller responsible for initiating the audio fingerprinting process.
 *
 * <p>This controller serves as the interface adapter layer component within
 * the Clean Architecture structure. Its primary role is to translate a
 * user's request (e.g., from the UI or an external interface) into a call to
 * the fingerprinting use case. The fingerprinting use case analyzes audio
 * input, generates a fingerprint, and attempts to match it against a database
 * of known songs for identification.</p>
 *
 * <p>The controller itself does not contain any business logic — it simply
 * delegates execution to the use case interactor provided via constructor
 * injection, ensuring loose coupling and testability.</p>
 */
public class FingerprintController {

    private final FingerprintIB fingerprinterUseCaseInteractor;

    /**
     * Constructs a new {@code FingerprintController} with the specified
     * fingerprinting use case interactor.
     *
     * @param fingerprinterUseCaseInteractor
     *         the interactor responsible for executing the audio fingerprinting
     *         use case; must not be {@code null}
     */
    public FingerprintController(FingerprintIB fingerprinterUseCaseInteractor) {
        this.fingerprinterUseCaseInteractor = fingerprinterUseCaseInteractor;
    }

    /**
     * Executes the audio fingerprinting use case.
     *
     * <p>This method triggers the process that analyzes the provided audio
     * sample, computes its fingerprint, and attempts to identify the song
     * associated with the input. Any results or errors are handled within the
     * use case layer or passed back to the calling presenter.</p>
     */
    public void execute() {
        fingerprinterUseCaseInteractor.execute();
    }
}
