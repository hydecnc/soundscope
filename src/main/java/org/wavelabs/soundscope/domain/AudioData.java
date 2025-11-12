package org.wavelabs.soundscope.domain;

/**
 * Entity representing audio data.
 * Pure domain object with no external dependencies.
 */
public class AudioData {
    private final double[] amplitudeSamples;
    private final String filePath;
    private final long durationMillis;
    private final int sampleRate;
    private final int channels;
    
    public AudioData(double[] amplitudeSamples, String filePath, long durationMillis, 
                     int sampleRate, int channels) {
        this.amplitudeSamples = amplitudeSamples;
        this.filePath = filePath;
        this.durationMillis = durationMillis;
        this.sampleRate = sampleRate;
        this.channels = channels;
    }
    
    public double[] getAmplitudeSamples() {
        return amplitudeSamples;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public long getDurationMillis() {
        return durationMillis;
    }
    
    public int getSampleRate() {
        return sampleRate;
    }
    
    public int getChannels() {
        return channels;
    }
    
    public double getDurationSeconds() {
        return durationMillis / 1000.0;
    }
}

