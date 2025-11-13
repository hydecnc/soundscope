package org.wavelabs.soundscope.infrastructure;

import org.wavelabs.soundscope.entity.AudioRecording;

import java.io.IOException;

public interface FileSaver {
    boolean save(String filePath, AudioRecording audioRecording) throws IOException;
}
