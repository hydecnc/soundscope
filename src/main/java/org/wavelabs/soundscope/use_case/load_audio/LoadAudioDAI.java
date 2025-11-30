package org.wavelabs.soundscope.use_case.load_audio;

import org.wavelabs.soundscope.entity.AudioData;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public interface LoadAudioDAI {
    // retroactive - changed method to match ProcessAudioDAI to load waveform.
    AudioData processAudioFile(File file) throws UnsupportedAudioFileException, IOException;
}
