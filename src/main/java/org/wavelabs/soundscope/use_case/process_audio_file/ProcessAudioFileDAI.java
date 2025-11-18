package org.wavelabs.soundscope.use_case.process_audio_file;

import org.wavelabs.soundscope.entity.AudioData;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Data Access Interface for ProcessAudioFileUseCase.
 * Defines the contract for accessing audio file data.
 * Implemented by the Data Access layer.
 */
public interface ProcessAudioFileDAI {
    /**
     * Processes an audio file and extracts amplitude samples.
     * 
     * @param file The audio file to process
     * @return AudioData containing amplitude samples and metadata
     * @throws UnsupportedAudioFileException if the audio format is not supported
     * @throws IOException if the file cannot be read or is corrupted
     */
    AudioData processAudioFile(File file) throws UnsupportedAudioFileException, IOException;
}


