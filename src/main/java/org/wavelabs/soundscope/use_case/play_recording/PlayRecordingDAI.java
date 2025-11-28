package org.wavelabs.soundscope.use_case.play_recording;

import org.wavelabs.soundscope.entity.AudioRecording;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * Data Access Interface for audio playback operations.
 * 
 * <p> This interface is the boundary between direct audio file operations and the use case interactor.
 * 
 * @throws IOException if there is an error reading the audio file
 * @throws UnsupportedAudioFileException if the audio file format is not supported
 * @throws NullPointerException if the sourcePath is null
 * 
 * @author Mark Peng
 */
public interface PlayRecordingDAI {

    /**
     * Loads an audio file from the specified source path.
     * @param sourcePath
     * @return
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws NullPointerException
     */
    AudioRecording loadAudio(String sourcePath) throws IOException, UnsupportedAudioFileException, NullPointerException;

    /**
     * Starts audio playback.
     * @throws IllegalStateException
     */
    void startPlayback() throws IllegalStateException;

    /**
     * Pauses audio playback.
     */
    void pausePlayback();

    /**
     * Stops audio playback. This is different than pausing, as it resets playback position to the beginning.
     */
    void stopPlayback();

    /**
     * Checks if the audio is currently playing.
     * @return true if audio is playing, false otherwise
     */
    boolean isPlaying();

    /**
     * Gets the number of frames that have been played so far.
     * @return The number of frames played
     */
    int getFramesPlayed();

    /**
     * Gets the total number of frames in the loaded audio.
     * @return The total number of frames
     */
    long getTotalFrames();
}
