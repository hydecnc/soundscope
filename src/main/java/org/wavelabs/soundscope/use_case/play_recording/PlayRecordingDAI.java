package org.wavelabs.soundscope.use_case.play_recording;

import org.wavelabs.soundscope.entity.AudioRecording;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public interface PlayRecordingDAI {
    AudioRecording loadAudio(String sourcePath) throws IOException, UnsupportedAudioFileException, NullPointerException;
    void startPlayback() throws IllegalStateException;
    void pausePlayback();
    void stopPlayback();
    boolean isPlaying();
    int getFramesPlayed();
    long getTotalFrames();
}
