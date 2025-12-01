package org.wavelabs.soundscope.infrastructure;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class JavaMicRecorder implements Recorder {
    private final AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, false);
    private final Object bufferLock = new Object();
    private volatile boolean isRecording;
    private Thread recordingThread;
    private TargetDataLine line;
    private ByteArrayOutputStream recordingByteData;
    private volatile byte[] currentBuffer;

    /**
     * Creates a new JavaMicRecorder, with default audio format.
     * At creation, checks for the audio format and audio line compatibility.
     *
     * @throws UnsupportedOperationException if the audio format is not supported
     * @throws IllegalStateException         if the audio line cannot be opened
     * @throws RuntimeException              if the audio format is not supported
     */
    public JavaMicRecorder() {

        // check if the audio line is compatible
        final DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            throw new RuntimeException("Audio Format not supported");
        }

        // audio line compatible: open audio line
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
        } 
        catch (LineUnavailableException ex) {
            throw new IllegalStateException("Failed to open audio line.");
        }
    }

    /**
     * Starts recording by starting the line,
     * and open a thread that will end as the recording ends.
     */
    @Override
    public void start() {
        if (isRecording) {
            return;
        }

        // set up variables for the recording
        isRecording = true;
        recordingByteData = new ByteArrayOutputStream();

        startRecordingThread();
    }

    @Override
    public void stop() {
        // updating the isRecording variable will terminate the while loop in startRecordingThread
        isRecording = false;
        try {
            // wait until thread ends completely
            recordingThread.join();
        } 
        catch (InterruptedException exception) {
            System.out.println("Recording thread interrupted");
        }

        System.out.println("Recording ended.");
        System.out.println("Byte size: " + recordingByteData.size());
        System.out.println("Recording length: " + (recordingByteData.size() / (44100.0 * 2 * 2)) + " seconds");
    }

    @Override
    public boolean isRecording() {
        return this.isRecording;
    }

    @Override
    public byte[] getRecordingBytes() {
        return recordingByteData.toByteArray();
    }

    public AudioFormat getAudioFormat() {
        return format;
    }

    /**
     * Creates a thread that will be active as long as isRecording is true.
     * At termination, line is closed.
     */
    private void startRecordingThread() {
        line.start();
        recordingThread = new Thread(this::recordingThread);
        recordingThread.start();

        System.out.println("Starting to record...");
    }

    /**
     * Extracted method for recording thread, in compliance with checkstyle lambda body length.
     */
    private void recordingThread() {
        int numBytesRead = 0;
        final byte[] data = new byte[line.getBufferSize() / 5];
        while (isRecording) {
            // read the next chunk of data from line
            numBytesRead = line.read(data, 0, data.length);
            recordingByteData.write(data, 0, numBytesRead);

            // Update current buffer for real-time waveform display
            synchronized (bufferLock) {
                currentBuffer = new byte[numBytesRead];
                System.arraycopy(data, 0, currentBuffer, 0, numBytesRead);
            }
        }

        line.stop();
        synchronized (bufferLock) {
            currentBuffer = null;
        }
    }

    /**
     * Gets the current audio buffer for real-time waveform display.
     *
     * @return The current audio buffer, or null if not recording
     */
    public byte[] getCurrentBuffer() {
        synchronized (bufferLock) {
            if (currentBuffer == null) {
                return null;
            }
            final byte[] copy = new byte[currentBuffer.length];
            System.arraycopy(currentBuffer, 0, copy, 0, currentBuffer.length);
            return copy;
        }
    }
}
