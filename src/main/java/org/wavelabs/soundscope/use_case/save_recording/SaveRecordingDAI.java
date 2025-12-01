package org.wavelabs.soundscope.use_case.save_recording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.FileSaver;

/**
 * Data Access Interface (DAI) for the save-recording use case.
 *
 * <p>This interface defines the gateway through which the save-recording
 * interactor accesses and updates the audio data to be saved, as well as the
 * mechanism responsible for writing that data to persistent storage.</p>
 *
 * <p>Implementations of this interface may store the in-memory recording,
 * interact with the filesystem, or provide mock objects for testing. By
 * depending only on this abstraction, the interactor remains independent of
 * concrete storage technologies and external frameworks.</p>
 */
public interface SaveRecordingDAI {

    /**
     * Returns the current in-memory audio recording to be saved.
     *
     * <p>This {@link AudioRecording} object typically contains the raw audio
     * bytes and the associated {@code AudioFormat} describing how the data
     * should be interpreted when written to a file.</p>
     *
     * @return the audio recording to be saved; never {@code null}
     */
    AudioRecording getAudioRecording();

    /**
     * Updates the in-memory audio recording.
     *
     * <p>This method allows the interactor or other components to replace the
     * stored recording with a new one, typically after recording has finished
     * or audio data has been processed.</p>
     *
     * @param audioRecording
     *         the audio recording to store; must not be {@code null}
     */
    void setAudioRecording(AudioRecording audioRecording);

    /**
     * Returns the file-saving mechanism associated with this data access layer.
     *
     * <p>The returned {@link FileSaver} is responsible for writing audio data
     * to disk in the desired format. The interactor delegates the persistence
     * operation to this component.</p>
     *
     * @return the file saver implementation; never {@code null}
     */
    FileSaver getFileSaver();

    /**
     * Sets the file saver implementation used for persisting audio recordings.
     *
     * <p>This allows flexibility in how audio files are written, enabling the
     * use of mock implementations during testing or switching between different
     * file formats or storage strategies.</p>
     *
     * @param fileSaver
     *         the file saver to use; must not be {@code null}
     */
    void setFileSaver(FileSaver fileSaver);
}
