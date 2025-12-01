package org.wavelabs.soundscope.use_case.start_recording;

import org.wavelabs.soundscope.infrastructure.Recorder;

/**
 * Data Access Interface (DAI) for the start-recording use case.
 *
 * <p>This interface defines how the start-recording interactor retrieves and
 * configures the {@link Recorder} responsible for capturing audio input.
 * Implementations of this interface provide access to the concrete recording
 * mechanism, whether it is a microphone-based recorder, a simulated recorder,
 * or a test double.</p>
 *
 * <p>By depending on this abstraction, the use case remains decoupled from any
 * specific audio-capture API or hardware implementation.</p>
 */
public interface StartRecordingDAI {

    /**
     * Returns the recorder used to capture audio input.
     *
     * <p>This recorder object provides methods for starting, stopping, and
     * querying the state of the recording session. It is typically injected or
     * configured during application startup.</p>
     *
     * @return the recorder instance; never {@code null}
     */
    Recorder getRecorder();

    /**
     * Updates the recorder used for capturing audio.
     *
     * <p>This method allows for dynamic replacement of the recorder, which is
     * particularly useful for testing or switching between different audio
     * input sources.</p>
     *
     * @param recorder
     *         the recorder instance to set; must not be {@code null}
     */
    void setRecorder(Recorder recorder);
}

