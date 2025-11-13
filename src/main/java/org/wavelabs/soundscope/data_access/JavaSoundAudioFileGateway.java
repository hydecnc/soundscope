package org.wavelabs.soundscope.data_access;

import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.use_case.process_audio_file.AudioFileGateway;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Java Sound API implementation of AudioFileGateway.
 * Framework-specific implementation for audio file processing using Java Sound API.
 * This class handles reading WAV audio files and converting them to PCM format.
 * 
 * <p>This implementation is part of the Frameworks & Drivers layer and provides
 * the concrete implementation of the AudioFileGateway interface defined in the
 * Use Case layer.
 */
public class JavaSoundAudioFileGateway implements AudioFileGateway {
    
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
     * @throws IOException if the file cannot be read, does not exist, or is corrupted
     */
    @Override
    public AudioData processAudioFile(File file) throws UnsupportedAudioFileException, IOException {
        if (file == null || !file.exists()) {
            throw new IOException("File does not exist: " + (file != null ? file.getPath() : "null"));
        }
        
        if (!file.canRead()) {
            throw new IOException("Cannot read file: " + file.getPath());
        }
        
        AudioInputStream audioInputStream = null;
        
        try {
            audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat originalFormat = audioInputStream.getFormat();
            
            AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                originalFormat.getSampleRate(),
                16,
                originalFormat.getChannels(),
                originalFormat.getChannels() * 2,
                originalFormat.getSampleRate(),
                false
            );
            
            if (!originalFormat.matches(targetFormat)) {
                audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
            }
            
            AudioFormat format = audioInputStream.getFormat();
            int sampleRate = (int) format.getSampleRate();
            int channels = format.getChannels();
            int frameSize = format.getFrameSize();
            long frameLength = audioInputStream.getFrameLength();
            
            byte[] audioBytes;
            int bytesRead;
            
            if (frameLength == AudioSystem.NOT_SPECIFIED || frameLength < 0) {
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
                audioBytes = new byte[(int) (frameLength * frameSize)];
                bytesRead = audioInputStream.read(audioBytes);
            }
            
            if (bytesRead < 0 || bytesRead == 0) {
                throw new IOException("File appears to be corrupted or empty: " + file.getPath());
            }
            
            long durationMillis = (long) ((frameLength * 1000.0) / sampleRate);
            
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
                    System.err.println("Warning: Failed to close audio stream: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Converts raw audio bytes to normalized amplitude samples.
     * 
     * <p>This method processes the audio bytes according to the audio format
     * (sample size, endianness, signed/unsigned) and converts them to normalized
     * amplitude values in the range [-1.0, 1.0]. Samples are downsampled to half
     * the original count for efficient processing.
     * 
     * @param audioBytes The raw audio byte data
     * @param format The audio format specification
     * @param bytesRead The number of bytes actually read from the audio stream
     * @param channels The number of audio channels
     * @return Array of normalized amplitude samples (downsampled to half)
     */
    private double[] convertToAmplitudeSamples(byte[] audioBytes, AudioFormat format, 
                                               int bytesRead, int channels) {
        int sampleSizeInBits = format.getSampleSizeInBits();
        boolean bigEndian = format.isBigEndian();
        
        int bytesPerSample = sampleSizeInBits / 8;
        int totalSamples = bytesRead / (bytesPerSample * channels);
        int downsampledCount = totalSamples / 2;
        
        double[] samples = new double[downsampledCount];
        
        for (int i = 0; i < downsampledCount; i++) {
            int sampleIndex = i * 2;
            int byteIndex = sampleIndex * bytesPerSample * channels;
            
            if (byteIndex + bytesPerSample * channels > bytesRead) {
                break;
            }
            
            long totalSample = 0;
            
            for (int c = 0; c < channels; c++) {
                int offset = byteIndex + c * bytesPerSample;
                
                if (offset + bytesPerSample > bytesRead) {
                    break;
                }
                
                int sample = 0;
                
                if (bytesPerSample == 2) {
                    if (bigEndian) {
                        sample = ((audioBytes[offset] << 8) | (audioBytes[offset + 1] & 0xFF));
                    } else {
                        sample = ((audioBytes[offset + 1] << 8) | (audioBytes[offset] & 0xFF));
                    }
                    
                    if (sample > 32767) {
                        sample -= 65536;
                    }
                } else if (bytesPerSample == 1) {
                    sample = audioBytes[offset] & 0xFF;
                    if (sample > 127) {
                        sample -= 256;
                    }
                }
                
                totalSample += sample;
            }
            
            double avgSample = totalSample / (double) channels;
            samples[i] = avgSample / 32768.0;
        }
        
        return samples;
    }
}

