package org.wavelabs.soundscope.data_access;

import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.infrastructure.Recorder;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformDAI;

import javax.sound.sampled.AudioFormat;

/**
 * Java Sound API implementation of DisplayRecordingWaveformDAI.
 * Framework-specific implementation for getting real-time recording buffer.
 * This class handles extracting amplitude samples from the current recording buffer.
 * 
 * <p>This implementation is part of the Frameworks & Drivers layer and provides
 * the concrete implementation of the DisplayRecordingWaveformDAI interface defined in the
 * Use Case layer.
 */
public class JavaSoundRecordingGateway implements DisplayRecordingWaveformDAI {
    private final Recorder recorder;
    
    /**
     * Constructs a JavaSoundRecordingGateway with the specified recorder.
     * 
     * @param recorder The recorder to get audio data from
     */
    public JavaSoundRecordingGateway(Recorder recorder) {
        this.recorder = recorder;
    }
    
    /**
     * Gets the current audio buffer from the recording and processes it to extract amplitude samples.
     * 
     * <p>This method gets the current recording buffer, converts it to amplitude samples,
     * and returns an AudioData object containing the processed audio information for display.
     * 
     * @return AudioData containing amplitude samples and metadata from the current recording buffer,
     *         or null if not currently recording
     */
    @Override
    public AudioData getCurrentRecordingBuffer() {
        if (!recorder.isRecording()) {
            return null;
        }
        
        byte[] buffer = null;
        if (recorder instanceof org.wavelabs.soundscope.infrastructure.JavaMicRecorder) {
            buffer = ((org.wavelabs.soundscope.infrastructure.JavaMicRecorder) recorder).getCurrentBuffer();
        } else {
            // Fallback: get all recorded bytes (less efficient but works)
            byte[] allBytes = recorder.getRecordingBytes();
            if (allBytes.length == 0) {
                return null;
            }
            // Get last chunk for display (e.g., last 0.1 seconds)
            int chunkSize = (int) (recorder.getAudioFormat().getSampleRate() * 
                                   recorder.getAudioFormat().getFrameSize() * 0.1);
            int start = Math.max(0, allBytes.length - chunkSize);
            buffer = new byte[allBytes.length - start];
            System.arraycopy(allBytes, start, buffer, 0, buffer.length);
        }
        
        if (buffer == null || buffer.length == 0) {
            return null;
        }
        
        AudioFormat format = recorder.getAudioFormat();
        int sampleRate = (int) format.getSampleRate();
        int channels = format.getChannels();
        
        double[] amplitudeSamples = convertToAmplitudeSamples(buffer, format, buffer.length, channels);
        
        // Calculate duration for the buffer
        long durationMillis = (long) ((amplitudeSamples.length * 4 * 1000.0) / sampleRate);
        
        return new AudioData(amplitudeSamples, "Recording", durationMillis, sampleRate, channels);
    }
    
    /**
     * Converts raw audio bytes to normalized amplitude samples.
     * 
     * <p>This method processes the audio bytes according to the audio format
     * (sample size, endianness, signed/unsigned) and converts them to normalized
     * amplitude values in the range [-1.0, 1.0]. Samples are downsampled to one-quarter
     * the original count for efficient processing.
     * 
     * @param audioBytes The raw audio byte data
     * @param format The audio format specification
     * @param bytesRead The number of bytes actually read from the audio stream
     * @param channels The number of audio channels
     * @return Array of normalized amplitude samples (downsampled to one-quarter)
     */
    private double[] convertToAmplitudeSamples(byte[] audioBytes, AudioFormat format, 
                                               int bytesRead, int channels) {
        int sampleSizeInBits = format.getSampleSizeInBits();
        boolean bigEndian = format.isBigEndian();
        
        int bytesPerSample = sampleSizeInBits / 8;
        int totalSamples = bytesRead / (bytesPerSample * channels);
        int downsampledCount = totalSamples / 4;
        
        if (downsampledCount == 0) {
            return new double[0];
        }
        
        double[] samples = new double[downsampledCount];
        
        for (int i = 0; i < downsampledCount; i++) {
            int sampleIndex = i * 4;
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

