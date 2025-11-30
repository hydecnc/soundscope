package org.wavelabs.soundscope.data_access;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingDAI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class JavaSoundPlaybackGateway implements PlayRecordingDAI {
    private final Object lock = new Object();
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

    @Override
    public AudioRecording loadAudio(String sourcePath)
        throws IOException, UnsupportedAudioFileException, NullPointerException {
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
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                bytesPerFrame = 1;
            }
            audioBuffer = new byte[1024 * bytesPerFrame];
            totalFramesRead = 0;
            currentRecording = buildRecording(file);
            return currentRecording;
        }
    }

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
}
