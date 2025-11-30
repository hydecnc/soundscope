package org.wavelabs.soundscope.use_case.save_recording;

import java.io.IOException;

public interface SaveRecordingDAI {
    boolean saveToFile(String filePath) throws IOException;
    boolean hasAudioRecording();
}
