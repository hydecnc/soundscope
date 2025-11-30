package org.wavelabs.soundscope.interface_adapter.load_audio;

import org.wavelabs.soundscope.entity.AudioData;

public class LoadAudioViewModel {
    private AudioData audioData;
    private String outputString;

    public AudioData getAudioData() {return audioData;}
    public void setAudioData(AudioData audioData) {this.audioData = audioData;}

    public String getOutputString() {return outputString;}
    public void setOutputString(String outputString) {}
}
