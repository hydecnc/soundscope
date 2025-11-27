package org.wavelabs.soundscope.data_access;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.FileSaver;
import org.wavelabs.soundscope.infrastructure.Recorder;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformDAI;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintDAI;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingDAI;
import org.wavelabs.soundscope.use_case.start_recording.StartRecordingDAI;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecordingDAI;

public class FileDAO implements StartRecordingDAI,
                                StopRecordingDAI,
                                SaveRecordingDAI,
                                DisplayRecordingWaveformDAI,
                                FingerprintDAI{
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
    public byte[] getAudioData() { return audioRecording.getData(); }

    @Override
    public AudioRecording getAudioRecording() { return audioRecording; }
    
    @Override
    public org.wavelabs.soundscope.entity.AudioData getCurrentRecordingBuffer() {
        if (recorder == null || !recorder.isRecording()) {
            return null;
        }
        
        // Delegate to the gateway implementation
        org.wavelabs.soundscope.data_access.JavaSoundRecordingGateway gateway = 
            new org.wavelabs.soundscope.data_access.JavaSoundRecordingGateway(recorder);
        return gateway.getCurrentRecordingBuffer();
    }
}
