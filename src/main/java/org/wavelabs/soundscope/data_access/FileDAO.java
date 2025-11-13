package org.wavelabs.soundscope.data_access;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.FileSaver;
import org.wavelabs.soundscope.infrastructure.Recorder;
import org.wavelabs.soundscope.use_cases.saveRecording.SaveRecordingDAI;
import org.wavelabs.soundscope.use_cases.startRecording.StartRecordingDAI;
import org.wavelabs.soundscope.use_cases.stopRecording.StopRecordingDAI;

public class FileDAO implements StartRecordingDAI,
                                StopRecordingDAI,
                                SaveRecordingDAI {
    private FileSaver fileSaver;
    private Recorder recorder;
    private AudioRecording audioRecording;

    @Override
    public void setFileSaver(FileSaver fileSaver) { this.fileSaver = fileSaver; }

    @Override
    public FileSaver getFileSaver() { return fileSaver; }

    @Override
    public void setRecorder(Recorder recorder) { this.recorder = recorder; }

    @Override
    public Recorder getRecorder() { return recorder; }

    @Override
    public void setAudioRecording(AudioRecording audioRecording) { this.audioRecording = audioRecording; }

    @Override
    public AudioRecording getAudioRecording() { return audioRecording; }
}
