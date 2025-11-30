package org.wavelabs.soundscope.data_access;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.FileSaver;
import org.wavelabs.soundscope.infrastructure.Recorder;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformDAI;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintDAI;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingDAI;
import org.wavelabs.soundscope.use_case.start_recording.StartRecordingDAI;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecordingDAI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class FileDAO implements StartRecordingDAI,
    StopRecordingDAI,
    SaveRecordingDAI,
    DisplayRecordingWaveformDAI,
    FingerprintDAI {
    private FileSaver fileSaver;
    private Recorder recorder;
    private AudioRecording audioRecording;

    @Override
    public FileSaver getFileSaver() {
        return fileSaver;
    }

    @Override
    public void setFileSaver(FileSaver fileSaver) {
        this.fileSaver = fileSaver;
    }

    @Override
    public Recorder getRecorder() {
        return recorder;
    }

    @Override
    public void setRecorder(Recorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public byte[] getAudioData() {
        if (audioRecording == null) {
            return null;
        }
        return audioRecording.getData();
    }

    @Override
    public AudioFormat getAudioFormat() {
        return audioRecording.getFormat();
    }

    @Override
    public AudioRecording getAudioRecording() {
        return audioRecording;
    }

    @Override
    public void setAudioRecording(AudioRecording audioRecording) {
        this.audioRecording = audioRecording;
    }

    @Override
    public AudioData getCurrentRecordingBuffer() {
        if (recorder == null || !recorder.isRecording()) {
            return null;
        }

        // Delegate to the gateway implementation
        org.wavelabs.soundscope.data_access.JavaSoundRecordingGateway gateway =
            new org.wavelabs.soundscope.data_access.JavaSoundRecordingGateway(recorder);
        return gateway.getCurrentRecordingBuffer();
    }

    /**
     * Loads audio from a file and creates an AudioRecording.
     * This should be called when a file is loaded to enable fingerprinting.
     *
     * @param file The audio file to load
     * @throws IOException                   if the file cannot be read
     * @throws UnsupportedAudioFileException if the audio format is not supported
     */
    public void loadAudioFromFile(File file) throws IOException, UnsupportedAudioFileException {
        try (AudioInputStream input = AudioSystem.getAudioInputStream(file);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(chunk)) != -1) {
                buffer.write(chunk, 0, bytesRead);
            }
            AudioFormat audioFormat = input.getFormat();
            this.audioRecording = new AudioRecording(buffer.toByteArray(), audioFormat);
        }
    }
}
