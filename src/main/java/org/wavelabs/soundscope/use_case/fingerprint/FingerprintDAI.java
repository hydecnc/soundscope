package org.wavelabs.soundscope.use_case.fingerprint;

import org.wavelabs.soundscope.entity.AudioData;

public interface FingerprintDAI {
    byte[] getAudioData();
    AudioData getCurrentRecordingBuffer();
}
