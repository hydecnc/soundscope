package org.wavelabs.soundscope.adapter.gateway;

import org.wavelabs.soundscope.domain.AudioData;
import org.wavelabs.soundscope.usecase.AudioFileGateway;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Java Sound API implementation of AudioFileGateway.
 * Framework-specific implementation for audio file processing.
 */
public class JavaSoundAudioFileGateway implements AudioFileGateway {
    
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
            
            if (!isFormatSupported(format)) {
                throw new UnsupportedAudioFileException(
                    "Unsupported audio format. Please use MP3 format only. " +
                    "Detected format: " + format.getEncoding() + ", " + format.getSampleRate() + " Hz"
                );
            }
            
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
    
    private boolean isFormatSupported(AudioFormat format) {
        AudioFormat.Encoding encoding = format.getEncoding();
        return encoding == AudioFormat.Encoding.PCM_SIGNED ||
               encoding == AudioFormat.Encoding.PCM_UNSIGNED ||
               encoding == AudioFormat.Encoding.PCM_FLOAT;
    }
    
    private double[] convertToAmplitudeSamples(byte[] audioBytes, AudioFormat format, 
                                               int bytesRead, int channels) {
        int sampleSizeInBits = format.getSampleSizeInBits();
        boolean bigEndian = format.isBigEndian();
        boolean signed = format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED;
        
        int bytesPerSample = sampleSizeInBits / 8;
        int totalSamples = bytesRead / (bytesPerSample * channels);
        
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
            
            long sampleValue = 0;
            for (int j = 0; j < bytesPerSample; j++) {
                int byteValue = audioBytes[byteIndex + j] & 0xFF;
                if (bigEndian) {
                    sampleValue = (sampleValue << 8) | byteValue;
                } else {
                    sampleValue = sampleValue | (byteValue << (j * 8));
                }
            }
            
            if (!signed && sampleValue >= maxAmplitude) {
                sampleValue -= 2 * maxAmplitude;
            }
            
            samples[i] = sampleValue / maxAmplitude;
        }
        
        return samples;
    }
}

