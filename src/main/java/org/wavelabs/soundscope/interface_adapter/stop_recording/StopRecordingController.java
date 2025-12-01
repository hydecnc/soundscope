package org.wavelabs.soundscope.interface_adapter.stop_recording;

import org.wavelabs.soundscope.use_case.stop_recording.StopRecordingIB;

/**
 * Controller responsible for stopping an active audio recording session.
 *
 * <p>This controller resides in the interface adapter layer of a Clean
 * Architecture design. It receives a request—typically from the UI—to stop
 * recording audio and delegates the operation to the {@link StopRecordingIB}
 * interactor, which contains the domain logic for finalizing a recording.</p>
 *
 * <p>The controller itself performs no recording logic. Its sole function is
 * to translate user actions into use case calls, ensuring a clean separation
 * between interface code and business rules.</p>
 */
public class StopRecordingController {

    private final StopRecordingIB stopRecordingInteractor;

    /**
     * Constructs a new {@code StopRecordingController} with the provided
     * stop-recording interactor.
     *
     * @param stopRecordingInteractor
     *         the use case interactor responsible for stopping audio recording;
     *         must not be {@code null}
     */
    public StopRecordingController(StopRecordingIB stopRecordingInteractor) {
        this.stopRecordingInteractor = stopRecordingInteractor;
    }

    /**
     * Executes the stop-recording use case.
     *
     * <p>This method delegates the stop operation to the interactor, which
     * halts audio capture, finalizes the recording buffer, and updates the
     * system’s recording state.</p>
     */
    public void execute() {
        stopRecordingInteractor.execute();
    }
}

