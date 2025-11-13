package org.wavelabs.soundscope.entity;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;

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

    public byte[] getData() { return data.clone(); }
    public int getSize() { return data.length; }
    public double getDurationSeconds() { return data.length / (44100.0 * 2 * 2); }
    public AudioFormat getFormat() { return format; }
}
