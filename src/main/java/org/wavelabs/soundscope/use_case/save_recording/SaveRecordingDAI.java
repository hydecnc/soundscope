package org.wavelabs.soundscope.use_case.save_recording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.FileSaver;

public interface SaveRecordingDAI {
    AudioRecording getAudioRecording();

    void setAudioRecording(AudioRecording audioRecording);

    FileSaver getFileSaver();

    void setFileSaver(FileSaver fileSaver);
}
