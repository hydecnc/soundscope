package org.wavelabs.soundscope.infrastructure;

import javax.sound.sampled.AudioFormat;

/**
 * Interface representing an audio recorder capable of capturing raw audio data.
 *
 * <p>This abstraction defines the essential operations for starting and
 * stopping audio recording, querying recording status, and retrieving the
 * captured audio data and its associated format. Implementations may wrap
 * platform-specific audio APIs such as {@code TargetDataLine}, microphone
 * drivers, or mock recorders for testing.</p>
 *
 * <p>By depending on this interface, other components—such as use case
 * interactors or controllers—remain independent of the underlying audio
 * system and can operate with any compatible recorder implementation.</p>
 */
public interface Recorder {

    /**
     * Begins capturing audio from the configured audio input source.
     *
     * <p>If the recorder is already active, calling this method may either
     * restart the recording or have no effect depending on the implementation.</p>
     */
    void start();

    /**
     * Stops audio capture.
     *
     * <p>After calling this method, no further audio bytes will be collected,
     * but previously recorded data remains available via
     * {@link #getRecordingBytes()}.</p>
     */
    void stop();

    /**
     * Returns whether the recorder is currently capturing audio.
     *
     * @return {@code true} if audio is actively being recorded,
     *         {@code false} otherwise
     */
    boolean isRecording();

    /**
     * Returns the raw audio bytes captured during the most recent recording
     * session.
     *
     * <p>The returned data typically reflects PCM-encoded audio consistent with
     * the format returned by {@link #getAudioFormat()}. The size and content of
     * the byte array depend on how long recording was active.</p>
     *
     * @return a byte array containing the recorded audio data; never
     *         {@code null} but possibly empty
     */
    byte[] getRecordingBytes();

    /**
     * Returns the {@link AudioFormat} describing how recorded audio bytes
     * should be interpreted.
     *
     * <p>This includes fields such as sample rate, sample size in bits, number
     * of channels, encoding, and endianness. This information is required to
     * correctly process, play, or transform the recorded audio.</p>
     *
     * @return the audio format used during recording; never {@code null}
     */
    AudioFormat getAudioFormat();
}
