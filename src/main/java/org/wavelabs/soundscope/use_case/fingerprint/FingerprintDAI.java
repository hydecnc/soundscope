package org.wavelabs.soundscope.use_case.fingerprint;

import javax.sound.sampled.AudioFormat;

import org.wavelabs.soundscope.entity.AudioData;

public interface FingerprintDAI {
    byte[] getAudioData();

    AudioFormat getAudioFormat();

    AudioData getCurrentRecordingBuffer();
}
