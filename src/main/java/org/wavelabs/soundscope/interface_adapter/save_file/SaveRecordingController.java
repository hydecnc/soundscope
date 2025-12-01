package org.wavelabs.soundscope.interface_adapter.save_file;

import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingIB;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingID;

/**
 * Controller responsible for initiating the audio recording save workflow.
 *
 * <p>This controller belongs to the interface adapter layer in a Clean
 * Architecture design. It receives requests—typically from the UI—to save the
 * current audio recording and delegates them to the {@link SaveRecordingIB}
 * interactor, which performs all domain-level logic involved in storing the
 * audio data to disk.</p>
 *
 * <p>The controller itself contains no business logic. Its sole responsibility
 * is to translate external input (such as a file path) into the appropriate
 * use case input data object.</p>
 */
public class SaveRecordingController {

    private final SaveRecordingIB saveRecordingInteractor;

    /**
     * Constructs a new {@code SaveRecordingController} with the given save
     * recording interactor.
     *
     * @param saveRecordingInteractor
     *         the use case interactor responsible for saving audio recordings;
     *         must not be {@code null}
     */
    public SaveRecordingController(SaveRecordingIB saveRecordingInteractor) {
        this.saveRecordingInteractor = saveRecordingInteractor;
    }

    /**
     * Executes the save-recording use case.
     *
     * <p>This method creates a {@link SaveRecordingID} containing the absolute
     * file path where the recording should be stored, then passes it to the
     * interactor for processing.</p>
     *
     * @param absolutePath
     *         the full path to the output file where the recording will be
     *         saved; must not be {@code null} or empty
     */
    public void execute(String absolutePath) {
        saveRecordingInteractor.execute(new SaveRecordingID(absolutePath));
    }
}

