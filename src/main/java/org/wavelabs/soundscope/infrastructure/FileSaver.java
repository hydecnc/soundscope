package org.wavelabs.soundscope.infrastructure;

import org.wavelabs.soundscope.entity.AudioRecording;
import java.io.IOException;

/**
 * FileSaver Interface used in Save Recording use case.
 */
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
