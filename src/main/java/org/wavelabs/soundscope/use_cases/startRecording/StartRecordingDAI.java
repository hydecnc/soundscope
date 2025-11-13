package org.wavelabs.soundscope.use_cases.startRecording;

import org.wavelabs.soundscope.infrastructure.Recorder;

public interface StartRecordingDAI {
    void setRecorder(Recorder recorder);
    Recorder getRecorder();
}
