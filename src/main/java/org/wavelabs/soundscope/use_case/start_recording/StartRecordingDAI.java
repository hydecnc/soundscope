package org.wavelabs.soundscope.use_case.start_recording;

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
    void startRecording() throws UnsupportedOperationException;
    boolean isRecording();
}

