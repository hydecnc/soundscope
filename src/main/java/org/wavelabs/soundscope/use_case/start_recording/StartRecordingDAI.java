package org.wavelabs.soundscope.use_case.start_recording;

import javax.sound.sampled.UnsupportedAudioFileException;

public interface StartRecordingDAI {
    void startRecording() throws UnsupportedAudioFileException;
    boolean isRecording();
}
