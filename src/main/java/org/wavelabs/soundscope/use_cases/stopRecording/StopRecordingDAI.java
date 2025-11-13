package org.wavelabs.soundscope.use_cases.stopRecording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.Recorder;

public interface StopRecordingDAI {
    void setRecorder(Recorder recorder);
    Recorder getRecorder();

    void setAudioRecording(AudioRecording audioRecording);
    AudioRecording getAudioRecording();
}
