package org.wavelabs.soundscope.use_case.play_recording;

/**
 * Input Data for the PlayRecording use case.
 * <p>
 * This class encapsulates the necessary information to initiate audio playback,
 * including the source path of the audio file and whether to restart playback from the beginning. It is used by the
 * use case interactor to determine how to handle playback requests.
 *
 * @author Mark Peng
 */
public class PlayRecordingID {
    private final String sourcePath;
    private final boolean restartFromBeginning;

    /**
     * Constructs a PlayRecordingID with the specified source path and restart option.
     * If restartFromBeginning is true, playback will restart from the beginning of the audio file.
     *
     * @param sourcePath           a string representing the path to audio file
     * @param restartFromBeginning whether to restart from the start or not
     */
    public PlayRecordingID(String sourcePath, boolean restartFromBeginning) {
        if (sourcePath == null || sourcePath.isBlank()) {
            throw new IllegalArgumentException("sourcePath must not be blank");
        }
        this.sourcePath = sourcePath;
        this.restartFromBeginning = restartFromBeginning;
    }

    /**
     * Gets the source path of the audio file to be played.
     * Needed to load the audio file for playback.
     *
     * @return The source path of the audio file as a String
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * Indicates whether playback should restart from the beginning of the audio file.
     *
     * @return true if playback should restart from the beginning, false otherwise
     */
    public boolean shouldRestartFromBeginning() {
        return restartFromBeginning;
    }
}
