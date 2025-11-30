package org.wavelabs.soundscope.use_case.stop_recording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.Recorder;

public interface StopRecordingDAI {
    Recorder getRecorder();

    void setRecorder(Recorder recorder);

    AudioRecording getAudioRecording();

    void setAudioRecording(AudioRecording audioRecording);
}
