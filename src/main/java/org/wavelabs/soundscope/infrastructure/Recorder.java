package org.wavelabs.soundscope.infrastructure;

import javax.sound.sampled.AudioFormat;

public interface Recorder {
    void start();
    void stop();
    boolean isRecording();
    byte[] getRecordingBytes();
    AudioFormat getAudioFormat();
}