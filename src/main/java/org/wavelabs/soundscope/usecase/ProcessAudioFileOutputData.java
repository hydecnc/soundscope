package org.wavelabs.soundscope.usecase;

import org.wavelabs.soundscope.domain.AudioData;

/**
 * Output data structure for ProcessAudioFileUseCase.
 * Represents the data passed from Use Case to Presenter.
 */
public class ProcessAudioFileOutputData {
    private final AudioData audioData;
    private final String fileName;
    
    public ProcessAudioFileOutputData(AudioData audioData, String fileName) {
        this.audioData = audioData;
        this.fileName = fileName;
    }
    
    public AudioData getAudioData() {
        return audioData;
    }
    
    public String getFileName() {
        return fileName;
    }
}

