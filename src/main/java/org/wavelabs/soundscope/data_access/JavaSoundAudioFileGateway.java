package org.wavelabs.soundscope.data_access;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileDAI;

/**
 * Java Sound API implementation of ProcessAudioFileDAI.
 * Framework-specific implementation for audio file processing using Java Sound API.
 * This class handles reading WAV audio files and converting them to PCM format.
 *
 * <p>This implementation is part of the Frameworks & Drivers layer and provides
 * the concrete implementation of the ProcessAudioFileDAI interface defined in the
 * Use Case layer.
 */
public class JavaSoundAudioFileGateway implements ProcessAudioFileDAI {

    // Class constants
    private static final int BIT_DEPTH_16 = 16;
    private static final int BUFFER_SIZE = 4096;
    private static final double MILLIS_PER_SECOND = 1000.0;
    private static final int DOWNSAMPLE_FACTOR = 256;
    private static final int BITS_PER_BYTE = 8;
    private static final int MASK_0XFF = 0xFF;
    private static final int MAX_16_BIT = 32767;
    private static final int UINT16 = 65536;
    private static final int MAX_8_BIT = 127;
    private static final double NORMALIZE_16BIT = 32768.0;

    /**
     * Processes an audio file and extracts amplitude samples.
     *
     * <p>This method reads the audio file, converts it to PCM format if necessary,
     * extracts amplitude samples, and returns an AudioData object containing
     * the processed audio information.
     *
     * @param file The audio file to process (must be a valid WAV file)
     * @return AudioData containing amplitude samples and metadata (duration, sample rate, channels)
     * @throws UnsupportedAudioFileException if the audio format is not supported by Java Sound API
     * @throws IOException                   if the file cannot be read, does not exist, or is corrupted
     */
    @Override
    public AudioData processAudioFile(File file) throws UnsupportedAudioFileException, IOException {
        if (file == null || !file.exists()) {
            final String pathStr;
            if (file != null) {
                pathStr = file.getPath();
            }
            else {
                pathStr = "null";
            }
            throw new IOException("File does not exist: " + pathStr);
        }

        if (!file.canRead()) {
            throw new IOException("Cannot read file: " + file.getPath());
        }

        AudioInputStream audioInputStream = null;

        try {
            audioInputStream = AudioSystem.getAudioInputStream(file);
            final AudioFormat originalFormat = audioInputStream.getFormat();

            final AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                originalFormat.getSampleRate(),
                BIT_DEPTH_16,
                originalFormat.getChannels(),
                originalFormat.getChannels() * 2,
                originalFormat.getSampleRate(),
                false
            );

            if (!originalFormat.matches(targetFormat)) {
                audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
            }

            final AudioFormat format = audioInputStream.getFormat();
            final int sampleRate = (int) format.getSampleRate();
            final int channels = format.getChannels();
            final int frameSize = format.getFrameSize();
            long frameLength = audioInputStream.getFrameLength();

            final byte[] audioBytes;
            int bytesRead;

            if (frameLength == AudioSystem.NOT_SPECIFIED || frameLength < 0) {
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                final byte[] tempBuffer = new byte[BUFFER_SIZE];
                int totalBytes = 0;

                while ((bytesRead = audioInputStream.read(tempBuffer)) != -1) {
                    buffer.write(tempBuffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }

                audioBytes = buffer.toByteArray();
                bytesRead = totalBytes;
                frameLength = bytesRead / frameSize;
            }
            else {
                audioBytes = new byte[(int) (frameLength * frameSize)];
                bytesRead = audioInputStream.read(audioBytes);
            }

            if (bytesRead < 0 || bytesRead == 0) {
                throw new IOException("File appears to be corrupted or empty: " + file.getPath());
            }

            final long durationMillis = (long) ((frameLength * MILLIS_PER_SECOND) / sampleRate);

            final double[] amplitudeSamples = convertToAmplitudeSamples(
                audioBytes,
                format,
                bytesRead,
                channels
            );

            return new AudioData(
                amplitudeSamples,
                file.getPath(),
                durationMillis,
                sampleRate,
                channels
            );

        }
        catch (UnsupportedAudioFileException | IOException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IOException("Error processing audio file: " + ex.getMessage(), ex);
        }
        finally {
            if (audioInputStream != null) {
                try {
                    audioInputStream.close();
                }
                catch (IOException ex) {
                    System.err.println("Warning: Failed to close audio stream: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Converts raw audio bytes to normalized amplitude samples.
     *
     * <p>This method processes the audio bytes according to the audio format
     * (sample size, endianness, signed/unsigned) and converts them to normalized
     * amplitude values in the range [-1.0, 1.0]. Samples are downsampled by 256
     * to match the live recording display format.
     *
     * @param audioBytes The raw audio byte data
     * @param format     The audio format specification
     * @param bytesRead  The number of bytes actually read from the audio stream
     * @param channels   The number of audio channels
     * @return Array of normalized amplitude samples (downsampled by 256)
     */
    private double[] convertToAmplitudeSamples(byte[] audioBytes, AudioFormat format,
                                               int bytesRead, int channels) {
        final int sampleSizeInBits = format.getSampleSizeInBits();
        final boolean bigEndian = format.isBigEndian();

        final int bytesPerSample = sampleSizeInBits / BITS_PER_BYTE;
        final int totalSamples = bytesRead / (bytesPerSample * channels);
        // Downsample by constant factor to match live recording format
        final int downsampledCount = totalSamples / DOWNSAMPLE_FACTOR;

        if (downsampledCount == 0) {
            return new double[0];
        }

        final double[] samples = new double[downsampledCount];

        for (int i = 0; i < downsampledCount; i++) {
            final int sampleIndex = i * DOWNSAMPLE_FACTOR;
            final int byteIndex = sampleIndex * bytesPerSample * channels;

            if (byteIndex + bytesPerSample * channels > bytesRead) {
                break;
            }

            long totalSample = 0;

            for (int c = 0; c < channels; c++) {
                final int offset = byteIndex + c * bytesPerSample;

                if (offset + bytesPerSample > bytesRead) {
                    break;
                }

                int sample = 0;

                if (bytesPerSample == 2) {
                    if (bigEndian) {
                        sample = (audioBytes[offset] << BITS_PER_BYTE) | audioBytes[offset + 1] & MASK_0XFF;
                    }
                    else {
                        sample = (audioBytes[offset + 1] << BITS_PER_BYTE) | audioBytes[offset] & MASK_0XFF;
                    }

                    if (sample > MAX_16_BIT) {
                        sample -= UINT16;
                    }
                }
                else if (bytesPerSample == 1) {
                    sample = audioBytes[offset] & MASK_0XFF;
                    if (sample > MAX_8_BIT) {
                        sample -= DOWNSAMPLE_FACTOR;
                    }
                }

                totalSample += sample;
            }

            final double avgSample = totalSample / (double) channels;
            samples[i] = avgSample / NORMALIZE_16BIT;
        }

        return samples;
    }
}

