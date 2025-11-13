package org.wavelabs.soundscope.use_cases.saveRecording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.FileSaver;

public interface SaveRecordingDAI {
    void setAudioRecording(AudioRecording audioRecording);
    AudioRecording getAudioRecording();

    void setFileSaver(FileSaver fileSaver);
    FileSaver getFileSaver();
}
