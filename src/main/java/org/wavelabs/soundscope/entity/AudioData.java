package org.wavelabs.soundscope.entity;

/**
 * Entity representing audio data.
 *
 * <p>Pure domain object with no external dependencies. This class is part of the
 * Enterprise Business Rules layer (Domain/Entities layer) and represents the core
 * business concept of audio data in the application.
 *
 * <p>An AudioData object contains:
 * <ul>
 *   <li>Amplitude samples: normalized audio amplitude values for waveform visualization</li>
 *   <li>File path: the location of the original audio file</li>
 *   <li>Duration: the length of the audio in milliseconds</li>
 *   <li>Sample rate: the number of samples per second</li>
 *   <li>Channels: the number of audio channels (mono, stereo, etc.)</li>
 * </ul>
 */
public class AudioData {
    private final double[] amplitudeSamples;
    private final String filePath;
    private final long durationMillis;
    private final int sampleRate;
    private final int channels;

    /**
     * Constructs an AudioData object with the specified parameters.
     *
     * @param amplitudeSamples Array of normalized amplitude samples (typically in range [-1.0, 1.0])
     * @param filePath         The file path of the original audio file
     * @param durationMillis   The duration of the audio in milliseconds
     * @param sampleRate       The sample rate in samples per second (Hz)
     * @param channels         The number of audio channels
     */
    public AudioData(double[] amplitudeSamples, String filePath, long durationMillis,
                     int sampleRate, int channels) {
        this.amplitudeSamples = amplitudeSamples;
        this.filePath = filePath;
        this.durationMillis = durationMillis;
        this.sampleRate = sampleRate;
        this.channels = channels;
    }

    /**
     * Gets the amplitude samples for waveform visualization.
     *
     * @return Array of normalized amplitude samples
     */
    public double[] getAmplitudeSamples() {
        return amplitudeSamples;
    }

    /**
     * Gets the file path of the original audio file.
     *
     * @return The file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Gets the duration of the audio in milliseconds.
     *
     * @return The duration in milliseconds
     */
    public long getDurationMillis() {
        return durationMillis;
    }

    /**
     * Gets the sample rate of the audio.
     *
     * @return The sample rate in samples per second (Hz)
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Gets the number of audio channels.
     *
     * @return The number of channels (1 for mono, 2 for stereo, etc.)
     */
    public int getChannels() {
        return channels;
    }

    /**
     * Gets the duration of the audio in seconds.
     *
     * @return The duration in seconds (as a decimal value)
     */
    public double getDurationSeconds() {
        final double millisPerSecond = 1000.0;
        return durationMillis / millisPerSecond;
    }
}
