package org.wavelabs.soundscope.interface_adapter.start_recording;

import org.wavelabs.soundscope.use_case.start_recording.StartRecordingIB;

/**
 * Controller responsible for initiating the audio recording workflow.
 *
 * <p>This controller belongs to the interface adapter layer in a Clean
 * Architecture structure. It receives a request—typically from the UI—to begin
 * capturing audio, and delegates the request to the {@link StartRecordingIB}
 * interactor, which performs the actual domain logic required to start
 * recording.</p>
 *
 * <p>The controller itself contains no recording logic. Its role is strictly
 * to translate user actions into a call to the use case interactor, ensuring
 * modularity and testability.</p>
 */
public class StartRecordingController {

    private final StartRecordingIB startRecordingInteractor;

    /**
     * Constructs a new {@code StartRecordingController} with the given
     * recording-start interactor.
     *
     * @param startRecordingInteractor
     *         the use case interactor responsible for initiating audio
     *         recording; must not be {@code null}
     */
    public StartRecordingController(StartRecordingIB startRecordingInteractor) {
        this.startRecordingInteractor = startRecordingInteractor;
    }

    /**
     * Executes the start-recording use case.
     *
     * <p>This method simply delegates to the use case interactor, which begins
     * capturing audio through the configured recording gateway.</p>
     */
    public void execute() {
        startRecordingInteractor.execute();
    }
}

