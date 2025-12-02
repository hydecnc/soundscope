package org.wavelabs.soundscope.use_case.start_recording;

/**
 * Output Boundary for the audio recording use case.
 *
 * <p>This interface defines how the recording interactor communicates updated
 * recording state back to the presentation layer. Implementations typically
 * convert the provided {@link RecordingOD} data into a view model or UI
 * updates.</p>
 *
 * <p>By abstracting the output channel, the recording use case remains
 * decoupled from UI frameworks such as Swing, JavaFX, or command-line
 * interfaces, ensuring testability and architectural separation.</p>
 */
public interface RecordingOB {

    /**
     * Sends updated recording state information to the presenter.
     *
     * <p>The {@link RecordingOD} output data may include fields such as:
     * <ul>
     *   <li>whether recording is active,</li>
     *   <li>the number of bytes captured,</li>
     *   <li>current recording duration,</li>
     *   <li>any relevant metadata about the recording session.</li>
     * </ul>
     * The presenter uses this information to update the UI or the application's
     * view model.</p>
     *
     * @param outputData
     *         structured recording-state data; must not be {@code null}
     */
    void updateRecordingState(RecordingOD outputData);

    /**
     * Error during save recording.
     * Present the according view.
     * @param message String message including the details of the error
     */
    void presentError(String message);
}

