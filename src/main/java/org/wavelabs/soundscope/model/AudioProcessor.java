package org.wavelabs.soundscope.model;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Processes audio files to extract amplitude samples for waveform visualization.
 * Handles reading audio files and converting them to amplitude data.
 */
public class AudioProcessor {
    
    /**
     * Reads an audio file and extracts amplitude samples.
     * 
     * @param file The audio file to process
     * @return AudioData containing amplitude samples and metadata
     * @throws UnsupportedAudioFileException if the audio format is not supported
     * @throws IOException if the file cannot be read or is corrupted
     */
    public AudioData processAudioFile(File file) throws UnsupportedAudioFileException, IOException {
        if (file == null || !file.exists()) {
            throw new IOException("File does not exist: " + (file != null ? file.getPath() : "null"));
        }
        
        if (!file.canRead()) {
            throw new IOException("Cannot read file: " + file.getPath());
        }
        
        AudioInputStream audioInputStream = null;
        
        try {
            // Open audio file (MP3 support via mp3spi library)
            audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat originalFormat = audioInputStream.getFormat();
            
            // Convert to PCM if needed (for MP3 and other compressed formats)
            AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                originalFormat.getSampleRate(),
                16, // 16-bit samples
                originalFormat.getChannels(),
                originalFormat.getChannels() * 2, // 2 bytes per sample
                originalFormat.getSampleRate(),
                false // little-endian
            );
            
            // Convert compressed formats (like MP3) to PCM
            if (!originalFormat.matches(targetFormat)) {
                audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
            }
            
            AudioFormat format = audioInputStream.getFormat();
            
            // Check if format is supported (should be PCM after conversion)
            if (!isFormatSupported(format)) {
                throw new UnsupportedAudioFileException(
                    "Unsupported audio format. Please use MP3 format only. " +
                    "Detected format: " + format.getEncoding() + ", " + format.getSampleRate() + " Hz"
                );
            }
            
            // Get audio properties
            int sampleRate = (int) format.getSampleRate();
            int channels = format.getChannels();
            int frameSize = format.getFrameSize();
            long frameLength = audioInputStream.getFrameLength();
            
            // For MP3 and some formats, frame length might be unknown (AudioSystem.NOT_SPECIFIED)
            // In that case, we'll read the entire stream
            byte[] audioBytes;
            int bytesRead;
            
            if (frameLength == AudioSystem.NOT_SPECIFIED || frameLength < 0) {
                // Read entire stream (for MP3 and formats with unknown length)
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] tempBuffer = new byte[4096];
                int totalBytes = 0;
                
                while ((bytesRead = audioInputStream.read(tempBuffer)) != -1) {
                    buffer.write(tempBuffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }
                
                audioBytes = buffer.toByteArray();
                bytesRead = totalBytes;
                frameLength = bytesRead / frameSize;
            } else {
                // Read known length
                audioBytes = new byte[(int) (frameLength * frameSize)];
                bytesRead = audioInputStream.read(audioBytes);
            }
            
            if (bytesRead < 0 || bytesRead == 0) {
                throw new IOException("File appears to be corrupted or empty: " + file.getPath());
            }
            
            // Calculate duration
            long durationMillis = (long) ((frameLength * 1000.0) / sampleRate);
            
            // Convert bytes to amplitude samples
            double[] amplitudeSamples = convertToAmplitudeSamples(
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
            
        } catch (UnsupportedAudioFileException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Error processing audio file: " + e.getMessage(), e);
        } finally {
            if (audioInputStream != null) {
                try {
                    audioInputStream.close();
                } catch (IOException e) {
                    // Log but don't throw - file processing may have succeeded
                    System.err.println("Warning: Failed to close audio stream: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Checks if the audio format is supported.
     */
    private boolean isFormatSupported(AudioFormat format) {
        AudioFormat.Encoding encoding = format.getEncoding();
        
        // Support common PCM formats
        return encoding == AudioFormat.Encoding.PCM_SIGNED ||
               encoding == AudioFormat.Encoding.PCM_UNSIGNED ||
               encoding == AudioFormat.Encoding.PCM_FLOAT;
    }
    
    /**
     * Converts raw audio bytes to normalized amplitude samples (-1.0 to 1.0).
     * Downsamples to approximately 3000 samples for performance.
     */
    private double[] convertToAmplitudeSamples(byte[] audioBytes, AudioFormat format, 
                                               int bytesRead, int channels) {
        int sampleSizeInBits = format.getSampleSizeInBits();
        boolean bigEndian = format.isBigEndian();
        boolean signed = format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED;
        
        int bytesPerSample = sampleSizeInBits / 8;
        int totalSamples = bytesRead / (bytesPerSample * channels);
        
        // Downsample to approximately 3000 samples (for performance)
        int maxSamples = 3000;
        int step = Math.max(1, totalSamples / maxSamples);
        int downsampledCount = totalSamples / step;
        
        double[] samples = new double[downsampledCount];
        double maxAmplitude = Math.pow(2, sampleSizeInBits - 1);
        
        for (int i = 0; i < downsampledCount; i++) {
            int sampleIndex = i * step;
            int byteIndex = sampleIndex * bytesPerSample * channels;
            
            if (byteIndex + bytesPerSample > bytesRead) {
                break;
            }
            
            // Read sample value based on format
            long sampleValue = 0;
            for (int j = 0; j < bytesPerSample; j++) {
                int byteValue = audioBytes[byteIndex + j] & 0xFF;
                if (bigEndian) {
                    sampleValue = (sampleValue << 8) | byteValue;
                } else {
                    sampleValue = sampleValue | (byteValue << (j * 8));
                }
            }
            
            // Convert to signed if needed
            if (!signed && sampleValue >= maxAmplitude) {
                sampleValue -= 2 * maxAmplitude;
            }
            
            // Normalize to -1.0 to 1.0
            samples[i] = sampleValue / maxAmplitude;
        }
        
        return samples;
    }
}

