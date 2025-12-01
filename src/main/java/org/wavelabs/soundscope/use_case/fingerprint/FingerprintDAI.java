package org.wavelabs.soundscope.use_case.fingerprint;

import javax.sound.sampled.AudioFormat;

/**
 * Data Access Interface (DAI) for retrieving audio input required by the fingerprinting use case.
 *
 * <p>
 * This interface defines the gateway through which the fingerprinting interactor obtains raw audio
 * data, metadata, and buffered recording information. Implementations may source audio from the
 * microphone, pre-recorded files, streams, or any other audio provider.
 * </p>
 *
 * <p>
 * By depending on this abstraction, the fingerprinting use case remains decoupled from specific I/O
 * mechanisms, enabling easy substitution for testing, platform-specific audio systems, or mock data
 * feeds.
 * </p>
 */
public interface FingerprintDAI {

    /**
     * Returns the latest raw audio data captured from the audio source.
     *
     * <p>
     * The returned byte array typically represents PCM audio samples or another format defined by
     * {@link #getAudioFormat()}. The size and structure of the data depend on the underlying audio
     * implementation.
     * </p>
     *
     * @return a byte array containing the most recently retrieved audio samples; never
     *         {@code null}, though possibly empty
     */
    byte[] getAudioData();

    /**
     * Returns the {@link AudioFormat} describing the structure of the audio data being captured.
     *
     * <p>
     * This includes details such as sample rate, sample size, channels, encoding type, and
     * endianness. The fingerprinting algorithm relies on this metadata to correctly interpret and
     * process the raw audio bytes.
     * </p>
     *
     * @return the audio format of the current audio stream; never {@code null}
     */
    AudioFormat getAudioFormat();
}

