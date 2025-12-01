package org.wavelabs.soundscope.use_case.stop_recording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.Recorder;

/**
 * Data Access Interface (DAI) for the stop-recording use case.
 *
 * <p>This interface provides the gateway through which the stop-recording
 * interactor accesses both the active {@link Recorder} and the in-memory
 * {@link AudioRecording} being constructed during the recording session.</p>
 *
 * <p>Implementations may wrap platform audio APIs, hold references to the
 * current recording buffer, or provide mock objects for testing. By depending
 * only on this abstraction, the use case remains decoupled from concrete audio
 * systems and storage mechanisms.</p>
 */
public interface StopRecordingDAI {

    /**
     * Returns the recorder currently used for capturing audio.
     *
     * <p>This recorder is expected to be in a recording state prior to the
     * stop operation, as the stop-recording use case relies on it to finalize
     * the captured audio data.</p>
     *
     * @return the active recorder; never {@code null}
     */
    Recorder getRecorder();

    /**
     * Updates or replaces the recorder used during recording.
     *
     * <p>This allows different recorder implementations (e.g., actual hardware,
     * mock recorder, or simulated input) to be configured at runtime or during
     * testing.</p>
     *
     * @param recorder
     *         the recorder to set; must not be {@code null}
     */
    void setRecorder(Recorder recorder);

    /**
     * Returns the in-memory audio recording associated with the current
     * recording session.
     *
     * <p>This {@link AudioRecording} object typically stores the accumulated
     * audio bytes and format information built during recording.</p>
     *
     * @return the current audio recording; never {@code null}
     */
    AudioRecording getAudioRecording();

    /**
     * Updates the in-memory audio recording.
     *
     * <p>This method allows the use case to store the final result of the
     * recording session once audio capture has been stopped and processed.</p>
     *
     * @param audioRecording
     *         the audio recording to set; must not be {@code null}
     */
    void setAudioRecording(AudioRecording audioRecording);
}

