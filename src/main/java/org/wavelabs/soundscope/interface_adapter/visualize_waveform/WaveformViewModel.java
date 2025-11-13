package org.wavelabs.soundscope.interface_adapter.visualize_waveform;

import org.wavelabs.soundscope.entity.AudioData;

/**
 * View Model for waveform visualization.
 * 
 * <p>Data structure optimized for the View, containing the data to be displayed.
 * This class is part of the Interface Adapters layer and serves as a container
 * for data that the View needs to render the waveform and display metadata.
 * 
 * <p>The ViewModel is updated by the Presenter and observed by the Controller
 * to update the View accordingly.
 */
public class WaveformViewModel {
    private AudioData audioData;
    private String outputText;
    
    /**
     * Constructs a new WaveformViewModel with default placeholder text.
     */
    public WaveformViewModel() {
        this.outputText = "Most similar to \"Viva La Vida\"<br>Fingerprint: abE671deF";
    }
    
    /**
     * Gets the audio data to be visualized.
     * 
     * @return The AudioData object containing amplitude samples and metadata, or null if not set
     */
    public AudioData getAudioData() {
        return audioData;
    }
    
    /**
     * Sets the audio data to be visualized.
     * 
     * @param audioData The AudioData object containing amplitude samples and metadata
     */
    public void setAudioData(AudioData audioData) {
        this.audioData = audioData;
    }
    
    /**
     * Gets the output text to be displayed in the view.
     * 
     * @return The output text (may contain HTML formatting)
     */
    public String getOutputText() {
        return outputText;
    }
    
    /**
     * Sets the output text to be displayed in the view.
     * 
     * @param outputText The output text (may contain HTML formatting)
     */
    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }
}

