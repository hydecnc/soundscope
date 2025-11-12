package org.wavelabs.soundscope.usecase;

import org.wavelabs.soundscope.domain.AudioData;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Gateway interface for audio file operations.
 * Part of Application Business Rules layer - defines the interface that use cases depend on.
 * Implementations are in the adapter/framework layer.
 */
public interface AudioFileGateway {
    /**
     * Reads an audio file and extracts amplitude samples.
     * 
     * @param file The audio file to process
     * @return AudioData containing amplitude samples and metadata
     * @throws UnsupportedAudioFileException if the audio format is not supported
     * @throws IOException if the file cannot be read or is corrupted
     */
    AudioData processAudioFile(File file) throws UnsupportedAudioFileException, IOException;
}

