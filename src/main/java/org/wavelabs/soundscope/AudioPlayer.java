package org.wavelabs.soundscope;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Plays audio to the system's default speaker.
 */
public class AudioPlayer {
    private volatile boolean isPlaying;
    private Thread playbackThread;
    private File audioFile;
    private AudioInputStream audioInputStream;
    private AudioFormat format;
    private SourceDataLine line;
    private int totalFramesRead;
    private int bytesPerFrame;
    private byte[] audioBytes;
    private final Object lock = new Object();

    /**
     * Creates a new AudioPlayer.
     */
    public AudioPlayer() {
        isPlaying = false;
        totalFramesRead = 0;
    }

    /**
     * Loads a new audio file to the player and opens the source data line.
     *
     * @throws IllegalArgumentException if the audio format is not supported by the line
     * @throws IllegalStateException if the audio line cannot be opened
     */
    public void loadAudio(File audioFile) {
        synchronized (lock) {
            cleanUp();

            this.audioFile = audioFile;
            loadAudioInputStream();
            format = audioInputStream.getFormat();

            // Get the source line for playback
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                throw new IllegalArgumentException("Audio file format not supported for playback.");
            }
            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
            } catch (LineUnavailableException e) {
                throw new IllegalStateException("Failed to open Audio Line.", e);
            }

            bytesPerFrame = audioInputStream.getFormat().getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED)
                bytesPerFrame = 1;
            audioBytes = new byte[1024 * bytesPerFrame];

        }
    }

    /**
     * Starts playing the audio file that is currently loaded in.
     */
    public void startPlayback() {
        synchronized (lock) {
            if (audioFile == null)
                throw new IllegalStateException("No audio file loaded.");

            if (isPlaying)
                return;
            isPlaying = true;

            playbackThread = new Thread(() -> {
                try {
                    int numBytesRead;
                    int numFramesRead;
                    line.start();
                    // Try to read numBytes from the file.
                    while (isPlaying && (numBytesRead = audioInputStream.read(audioBytes)) != -1) {
                        numFramesRead = numBytesRead / bytesPerFrame;
                        totalFramesRead += numFramesRead;
                        line.write(audioBytes, 0, numBytesRead);
                    }

                    if (isPlaying) {
                        line.drain();
                        line.stop();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    line.stop();
                } finally {
                    isPlaying = false;
                }
            });
        }
        playbackThread.start();
    }

    /**
     * Pauses audio playback and keeps the current progress.
     */
    public void pausePlayback() {
        synchronized (lock) {
            isPlaying = false;
            if (line != null && line.isOpen())
                line.stop();
        }
    }

    /**
     * Reset audio playback to the start.
     */
    public void resetPlayback() {
        synchronized (lock) {
            if (audioFile == null)
                throw new IllegalStateException("No audio file loaded.");

            pausePlayback();
            totalFramesRead = 0;
            loadAudioInputStream();
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }


    public int getTotalFramesRead() {
        return totalFramesRead;
    }

    /**
     * Returns the total number of frames of the current audio input stream. If there is no audio
     * input stream, returns -1.
     */
    public long getTotalFrames() {
        if (audioInputStream == null)
            return -1;
        return audioInputStream.getFrameLength();
    }

    /**
     * Loads a fresh audio input stream from the file.
     * 
     * @see AudioInputStream for more information on audio input stream
     * @throws IllegalStateException if the audio input stream cannot be obtained from audio file
     */
    private void loadAudioInputStream() {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        } catch (UnsupportedAudioFileException | IOException | NullPointerException e) {
            throw new RuntimeException(
                    "Failed to get Audio Input Stream. Make sure the file provided was a valid Audio File.",
                    e);
        }
    }

    private void cleanUp() {
        pausePlayback();
        if (playbackThread != null) {
            try {
                playbackThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while waiting for playback thread to finish: "
                        + e.getMessage());
            }
        }
        if (audioInputStream != null) {
            try {
                audioInputStream.close();
            } catch (IOException e) {
                System.err.println("Failed to close audio input stream: " + e.getMessage());
            }
        }
        if (line != null && line.isOpen())
            line.close();
        line = null;
    }
}

