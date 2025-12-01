package org.wavelabs.soundscope.entity;

import javax.sound.sampled.AudioFormat;

/**
 * Audio Recording entity class.
 */
public class AudioRecording {
    private final byte[] data;
    private final AudioFormat format;

    public AudioRecording(byte[] data, AudioFormat format) {
        this.data = data;
        this.format = format;
    }

    public byte[] getData() {
        return data.clone();
    }

    public int getSize() {
        return data.length;
    }

    /**
     * Gets the duration in seconds from data length.
     * @return a double representing the seconds
     */
    public double getDurationSeconds() {
        final double sampleRate = format.getSampleRate();
        final int bytesPerSample = format.getSampleSizeInBits() / 8;
        final int channels = format.getChannels();
        final double bytesPerSecond = sampleRate * bytesPerSample * channels;
        return data.length / bytesPerSecond;
    }

    public AudioFormat getFormat() {
        return format;
    }
}
