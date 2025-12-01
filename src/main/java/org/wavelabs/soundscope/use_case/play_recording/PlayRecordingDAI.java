package org.wavelabs.soundscope.use_case.play_recording;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.wavelabs.soundscope.entity.AudioRecording;

/**
 * Data Access Interface for audio playback operations.
 *
 * <p>This interface is the boundary between direct audio file operations and the use case interactor.
 *
 * @author Mark Peng
 */
public interface PlayRecordingDAI {

    /**
     * Loads an audio file from the specified source path.
     *
     * @param sourcePath the path of the audio file
     * @return an AudioRecording object housing audio data
     * @throws IOException                   for IO errors
     * @throws UnsupportedAudioFileException for non WAV files
     * @throws NullPointerException          for Null sourcePaths
     */
    AudioRecording loadAudio(String sourcePath) throws IOException, UnsupportedAudioFileException, NullPointerException;

    /**
     * Starts audio playback.
     *
     * @throws IllegalStateException for broken audio playing states
     */
    void startPlayback() throws IllegalStateException;

    /**
     * Pauses audio playback.
     */
    void pausePlayback();

    /**
     * Stops audio playback. This is different from pausing, as it resets playback position to the beginning.
     */
    void stopPlayback();

    /**
     * Checks if the audio is currently playing.
     *
     * @return true if audio is playing, false otherwise
     */
    boolean isPlaying();

    /**
     * Gets the number of frames that have been played so far.
     *
     * @return The number of frames played
     */
    int getFramesPlayed();

    /**
     * Gets the total number of frames in the loaded audio.
     *
     * @return The total number of frames
     */
    long getTotalFrames();
}
