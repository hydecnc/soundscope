package org.wavelabs.soundscope.use_case.start_recording;

import org.wavelabs.soundscope.infrastructure.Recorder;

public interface StartRecordingDAI {
    Recorder getRecorder();

    void setRecorder(Recorder recorder);
}
