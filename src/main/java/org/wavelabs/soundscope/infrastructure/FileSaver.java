package org.wavelabs.soundscope.infrastructure;

import java.io.IOException;

import org.wavelabs.soundscope.entity.AudioRecording;

public interface FileSaver {
    /**
     * Saves the audio recording to file.
     * @param filePath the string file path
     * @param audioRecording the AudioRecording object
     * @return boolean, true if success false if otherwise
     * @throws IOException if file cannot be saved
     */
    boolean save(String filePath, AudioRecording audioRecording) throws IOException;
}
