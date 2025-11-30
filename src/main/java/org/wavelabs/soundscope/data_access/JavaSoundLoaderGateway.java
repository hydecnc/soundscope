package org.wavelabs.soundscope.data_access;

import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingDAI;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileDAI;
import org.wavelabs.soundscope.use_case.load_audio.LoadAudioDAI;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class JavaSoundLoaderGateway implements PlayRecordingDAI, ProcessAudioFileDAI, LoadAudioDAI {
    private volatile boolean isPlaying;
    private Thread playbackThread;
    private File audioFile;
    private AudioInputStream audioInputStream;
    private AudioFormat format;
    private SourceDataLine line;
    private volatile int totalFramesRead;
    private int bytesPerFrame;
    private byte[] audioBuffer;
    private AudioRecording currentRecording;
    private final Object lock = new Object();

    // below are the two methods that loads the audio in both the Playback and AudioFile gateway.
    // both methods were preserved, due to the different datatypes AudioRecording & AudioData.

    @Override
    public AudioRecording loadAudio(String sourcePath) throws IOException, UnsupportedAudioFileException, NullPointerException {
        Objects.requireNonNull(sourcePath, "sourcePath must not be null");
        File file = new File(sourcePath);
        if (!file.exists()) {
            throw new IOException("Audio file not found: " + sourcePath);
        }

        synchronized (lock) {
            cleanUp();
            this.audioFile = file;
            openPlaybackStream();
            format = audioInputStream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                throw new UnsupportedAudioFileException("Audio format not supported for playback");
            }

            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
            } catch (LineUnavailableException e) {
                throw new IOException("Unable to open audio output line", e);
            }

            bytesPerFrame = audioInputStream.getFormat().getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED)
                bytesPerFrame = 1;
            audioBuffer = new byte[1024 * bytesPerFrame];
            totalFramesRead = 0;
            currentRecording = buildRecording(file);
            return currentRecording;
        }
    }

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

    // Overloaded method loadAudio that converts file to filepath to call method above
    @Override
    public AudioRecording loadAudio(File file) throws UnsupportedAudioFileException, IOException {
        return loadAudio(file.getPath());
    }

    // below are the methods needed for the PlayRecordingDAI

    @Override
    public void startPlayback() throws IllegalStateException {
        synchronized (lock) {
            if (audioInputStream == null || line == null) {
                throw new IllegalStateException("No audio loaded for playback");
            }
            if (isPlaying) {
                return;
            }
            isPlaying = true;
            playbackThread = new Thread(this::streamAudioToLine, "audio-playback-thread");
            playbackThread.start();
        }
    }

    @Override
    public void pausePlayback() {
        synchronized (lock) {
            isPlaying = false;
            if (line != null && line.isOpen()) {
                line.stop();
            }
        }
    }

    @Override
    public void stopPlayback() {
        synchronized (lock) {
            isPlaying = false;
            if (line != null && line.isOpen()) {
                line.stop();
            }
            joinPlaybackThread();
            totalFramesRead = 0;
            if (audioFile != null) {
                try {
                    openPlaybackStream();
                } catch (IOException | UnsupportedAudioFileException e) {
                    throw new IllegalStateException("Failed to reset playback stream", e);
                }
            }
        }
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public int getFramesPlayed() {
        return totalFramesRead;
    }

    // below are the methods needed for the ProcessAudioFileDAI

    @Override
    public long getTotalFrames() {
        synchronized (lock) {
            return audioInputStream != null ? audioInputStream.getFrameLength() : -1;
        }
    }

    private void streamAudioToLine() {
        try {
            int numBytesRead;
            int numFramesRead;
            if (line == null) {
                return;
            }
            line.start();
            while (isPlaying && (numBytesRead = audioInputStream.read(audioBuffer)) != -1) {
                numFramesRead = numBytesRead / bytesPerFrame;
                totalFramesRead += numFramesRead;
                line.write(audioBuffer, 0, numBytesRead);
            }

            if (isPlaying) {
                line.drain();
                line.stop();
            }
        } catch (IOException e) {
            if (line != null) {
                line.stop();
            }
        } finally {
            isPlaying = false;
        }
    }

    private void openPlaybackStream() throws IOException, UnsupportedAudioFileException {
        closeStream();
        audioInputStream = AudioSystem.getAudioInputStream(audioFile);
    }

    private AudioRecording buildRecording(File file) throws IOException, UnsupportedAudioFileException {
        try (AudioInputStream input = AudioSystem.getAudioInputStream(file);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(chunk)) != -1) {
                buffer.write(chunk, 0, bytesRead);
            }
            AudioFormat audioFormat = input.getFormat();
            return new AudioRecording(buffer.toByteArray(), audioFormat);
        }
    }

    private void cleanUp() {
        pausePlayback();
        joinPlaybackThread();
        closeStream();
        if (line != null) {
            line.close();
            line = null;
        }
    }

    private void joinPlaybackThread() {
        if (playbackThread != null) {
            try {
                playbackThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            playbackThread = null;
        }
    }

    private void closeStream() {
        if (audioInputStream != null) {
            try {
                audioInputStream.close();
            } catch (IOException ignored) {
            }
            audioInputStream = null;
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
     * @param format The audio format specification
     * @param bytesRead The number of bytes actually read from the audio stream
     * @param channels The number of audio channels
     * @return Array of normalized amplitude samples (downsampled by 256)
     */
    private double[] convertToAmplitudeSamples(byte[] audioBytes, AudioFormat format,
                                               int bytesRead, int channels) {
        int sampleSizeInBits = format.getSampleSizeInBits();
        boolean bigEndian = format.isBigEndian();

        int bytesPerSample = sampleSizeInBits / 8;
        int totalSamples = bytesRead / (bytesPerSample * channels);
        // Downsample by 256 to match live recording format
        int downsampledCount = totalSamples / 256;

        if (downsampledCount == 0) {
            return new double[0];
        }

        double[] samples = new double[downsampledCount];

        for (int i = 0; i < downsampledCount; i++) {
            int sampleIndex = i * 256;
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
