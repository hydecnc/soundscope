package org.wavelabs.soundscope.use_case.play_recording;

/**
 * Input Boundary for the audio playback use case.
 *
 * <p>This interface defines the operations that the playback controller may
 * request from the application’s playback interactor. It represents the entry
 * point into the playback use case within a Clean Architecture setup.</p>
 *
 * <p>Implementations of this interface contain the business logic for managing
 * audio playback, including starting playback from a specified source,
 * pausing, and stopping. By depending only on this abstraction, the UI or
 * controller layer remains decoupled from the actual playback mechanics or
 * audio system implementation.</p>
 */
public interface PlayRecordingIB {

    /**
     * Begins or resumes audio playback using the specified audio source.
     *
     * <p>The provided {@link PlayRecordingID} contains all information
     * required to initiate playback, such as the path to the audio file and
     * whether playback should begin at the start or resume from a previous
     * position.</p>
     *
     * @param audioSource
     *         the input data describing the audio to be played; must not be
     *         {@code null}
     */
    void play(PlayRecordingID audioSource);

    /**
     * Pauses the currently playing audio while preserving the playback position.
     *
     * <p>The next call to {@link #play(PlayRecordingID)} may resume playback
     * from the paused position, depending on the implementation and request.</p>
     */
    void pause();

    /**
     * Stops audio playback entirely.
     *
     * <p>This operation typically resets playback state and discards any stored
     * playback position, preparing the system for a fresh playback request.</p>
     */
    void stop();
}

