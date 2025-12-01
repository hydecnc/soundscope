package org.wavelabs.soundscope.use_case.identify;

/**
 * Output Boundary for the song identification use case.
 *
 * <p>This interface defines the methods through which the identification
 * interactor communicates results back to the interface adapter layer.
 * Implementations typically include presenters that transform raw output data
 * into a view model suitable for the UI.</p>
 *
 * <p>The output boundary enables the use case to remain independent of UI
 * frameworks, presentation logic, or external delivery mechanisms. It
 * expresses the exact information the interactor is allowed to send outward,
 * ensuring clean separation of concerns.</p>
 */
public interface IdentifyOB {

    /**
     * Sends the successfully identified song's attributes to the presenter.
     *
     * <p>The {@link IdentifyOD} object usually includes metadata such as
     * the song title, artist, confidence score, and any additional information
     * derived from the identification process. The presenter is responsible for
     * formatting this data and delivering it to the user interface.</p>
     *
     * @param outputData
     *         structured data describing the identified song; must not be
     *         {@code null}
     */
    void updateSongAttributes(IdentifyOD outputData);

    /**
     * Presents an error message to indicate that the identification process
     * failed or could not be completed.
     *
     * <p>Errors may include missing fingerprints, audio processing issues,
     * database lookup failures, or unexpected exceptions. Implementations of
     * this method determine how the message is ultimately conveyed to the
     * user.</p>
     *
     * @param errorMessage
     *         a human-readable error message describing the failure; never
     *         {@code null} or empty
     */
    void presentError(String errorMessage);

}
