package org.wavelabs.soundscope.infrastructure;

import org.wavelabs.soundscope.entity.AudioRecording;
import java.io.IOException;

/**
 * FileSaver Interface used in Save Recording use case.
 */
public interface FileSaver {
    boolean save(String filePath, AudioRecording audioRecording) throws IOException;
}
