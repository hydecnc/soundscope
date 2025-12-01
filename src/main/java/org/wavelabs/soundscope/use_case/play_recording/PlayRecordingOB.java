package org.wavelabs.soundscope.use_case.play_recording;

/**
 * Output Boundary for the audio playback use case.
 *
 * <p>This interface defines how the playback interactor communicates results,
 * state changes, and errors back to the presentation layer. Implementations
 * typically transform these outputs into updates for the user interface or a
 * view model.</p>
 *
 * <p>By using this abstraction, the playback use case remains independent of
 * UI frameworks, allowing different presentation strategies (Swing, web, CLI,
 * tests) without modifying the interactor logic.</p>
 */
public interface PlayRecordingOB {

    /**
     * Indicates that audio playback has begun or resumed.
     *
     * <p>Presenters implementing this method typically update the UI to reflect
     * an active playback state (e.g., switching buttons or showing progress).</p>
     */
    void playbackStarted();

    /**
     * Indicates that playback has been paused.
     *
     * <p>Presenters may use this callback to update UI controls or internal
     * state to reflect that the user may resume playback later.</p>
     */
    void playbackPaused();

    /**
     * Indicates that playback has been fully stopped.
     *
     * <p>This is generally used when playback ends naturally or the user stops
     * it manually. Presenters may clear progress displays or reset buttons.</p>
     */
    void playbackStopped();

    /**
     * Reports an error that occurred during playback.
     *
     * <p>This may include I/O errors, invalid audio sources, decoder failures,
     * or any unexpected exceptions within the audio system. The presenter is
     * responsible for delivering the error message to the user in an
     * appropriate format.</p>
     *
     * @param message
     *         a human-readable description of the error; never {@code null}
     */
    void playbackError(String message);

    /**
     * Sends updated playback-related state to the presentation layer.
     *
     * <p>The {@link PlayRecordingOD} object typically includes data such as
     * current playback position, duration, or any state changes needed by the
     * view model. This method allows the UI to react to ongoing playback
     * events.</p>
     *
     * @param updateData
     *         structured output data representing the latest playback state;
     *         must not be {@code null}
     */
    void updateMainState(PlayRecordingOD updateData);
}
