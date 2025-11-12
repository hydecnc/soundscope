package org.wavelabs.soundscope.adapter.viewmodel;

import org.wavelabs.soundscope.domain.AudioData;

/**
 * View Model for waveform visualization.
 * Data structure optimized for the View, containing the data to be displayed.
 */
public class WaveformViewModel {
    private AudioData audioData;
    private String outputText;
    
    public WaveformViewModel() {
        this.outputText = "Most similar to \"Viva La Vida\"<br>Fingerprint: abE671deF";
    }
    
    public AudioData getAudioData() {
        return audioData;
    }
    
    public void setAudioData(AudioData audioData) {
        this.audioData = audioData;
    }
    
    public String getOutputText() {
        return outputText;
    }
    
    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }
}

