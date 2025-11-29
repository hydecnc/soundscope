package org.wavelabs.soundscope.use_case.play_recording;

public interface PlayRecordingIB {
    void play(PlayRecordingID audioSource);
    void pause();
    void stop();
}
