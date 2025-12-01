package org.wavelabs.soundscope.interface_adapter.play_recording;

import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingIB;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingID;

/**
 * Controller responsible for managing audio playback actions.
 *
 * <p>This controller acts as part of the interface adapter layer in a Clean
 * Architecture design. It receives playback commands from the UI or other
 * drivers and delegates them to the {@link PlayRecordingIB} interactor, which
 * handles the actual playback logic such as starting, pausing, and stopping audio.</p>
 *
 * <p>The controller itself contains no business logic; its sole responsibility
 * is to translate external input into properly formatted use case requests,
 * such as creating a {@link PlayRecordingID} object when starting playback.</p>
 */
public class PlayRecordingController {

    private final PlayRecordingIB playRecordingInteractor;

    /**
     * Constructs a new {@code PlayRecordingController} with the given playback
     * interactor.
     *
     * @param playRecordingInteractor
     *         the use case interactor that executes playback operations;
     *         must not be {@code null}
     */
    public PlayRecordingController(PlayRecordingIB playRecordingInteractor) {
        this.playRecordingInteractor = playRecordingInteractor;
    }

    /**
     * Stops audio playback completely.
     *
     * <p>This method delegates to the playback interactor, which terminates
     * any active audio stream and resets playback state as needed.</p>
     */
    public void stop() {
        playRecordingInteractor.stop();
    }

    /**
     * Pauses the currently playing audio.
     *
     * <p>This method signals the playback interactor to temporarily halt
     * playback while retaining the current playback position, allowing
     * resume behavior.</p>
     */
    public void pause() {
        playRecordingInteractor.pause();
    }

    /**
     * Begins or resumes audio playback from the specified audio source.
     *
     * <p>This method constructs a {@link PlayRecordingID} request containing
     * the path to the audio file and a flag indicating whether playback should
     * restart from the beginning. The request is forwarded to the playback
     * interactor to execute the appropriate playback action.</p>
     *
     * @param currentAudioSourcePath
     *         the file system path or resource location of the audio source;
     *         must not be {@code null}
     * @param restartFromBeginning
     *         {@code true} if playback should start from the beginning,
     *         {@code false} if playback may resume from the last paused position
     */
    public void play(String currentAudioSourcePath, boolean restartFromBeginning) {
        playRecordingInteractor.play(new PlayRecordingID(currentAudioSourcePath, restartFromBeginning));
    }
}
