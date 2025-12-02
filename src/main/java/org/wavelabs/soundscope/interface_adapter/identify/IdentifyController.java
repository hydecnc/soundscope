package org.wavelabs.soundscope.interface_adapter.identify;

import org.wavelabs.soundscope.use_case.identify.IdentifyIB;

/**
 * Controller responsible for initiating the audio identification process.
 *
 * <p>This controller belongs to the interface adapter layer of the Clean
 * Architecture design. It receives requests from the user interface or
 * external drivers and delegates them to the identification use case. The
 * identification use case typically analyzes an audio fingerprint, compares it
 * against a known database, and returns the most likely matching song or
 * recording.</p>
 *
 * <p>By acting as a thin translation layer, the controller contains no domain
 * logic of its own. Instead, it forwards calls to the injected
 * {@link IdentifyIB} interactor, ensuring separation of concerns and testable,
 * modular behavior.</p>
 */
public class IdentifyController {

    private final IdentifyIB identifyInteractor;

    /**
     * Constructs a new {@code IdentifyController} using the provided
     * identification interactor.
     *
     * @param identifyInteractor
     *         the use case interactor responsible for identifying a song from
     *         a previously computed audio fingerprint; must not be {@code null}
     */
    public IdentifyController(IdentifyIB identifyInteractor) {
        this.identifyInteractor = identifyInteractor;
    }

    /**
     * Triggers the audio identification workflow.
     *
     * <p>This method delegates to the identification use case, which attempts
     * to match the current audio fingerprint against the application's song
     * database. Any results or error information are handled by the use case's
     * output boundary or its associated presenter.</p>
     */
    public void identify() {
        identifyInteractor.identify();
    }
}
