package org.wavelabs.soundscope.use_case.stop_recording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.Recorder;

public interface StopRecordingDAI {
    void stopRecording();
}
