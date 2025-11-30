package org.wavelabs.soundscope.use_case.load_audio;

import org.wavelabs.soundscope.entity.AudioRecording;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public interface LoadAudioDAI {
    // retroactive - changed parameter "sourcePath" to match with PlayRecordingDAI for refactoring purposes.
    AudioRecording loadAudio(File file) throws IOException, UnsupportedAudioFileException, NullPointerException;
}
