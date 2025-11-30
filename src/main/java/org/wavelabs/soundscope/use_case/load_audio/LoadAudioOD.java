package org.wavelabs.soundscope.use_case.load_audio;
import org.wavelabs.soundscope.entity.AudioRecording;

public class LoadAudioOD {
    private final AudioRecording audioData;
    private final String audioPath;

    public LoadAudioOD(AudioRecording audioData, String audioPath) {
        this.audioData = audioData;
        this.audioPath = audioPath;
    }

    public AudioRecording getAudioData() {
        return audioData;
    }

    public String getAudioPath() {
        return audioPath;
    }
}
