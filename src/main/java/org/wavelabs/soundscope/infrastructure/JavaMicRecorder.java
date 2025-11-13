package org.wavelabs.soundscope.infrastructure;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

public class JavaMicRecorder implements Recorder {
    private volatile boolean isRecording = false;
    private Thread recordingThread;
    private TargetDataLine line;
    private ByteArrayOutputStream recordingByteData;
    private final AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, false);

    /**
     * Create a new JavaMicRecorder, with default audio format.
     * At creation, checks for the audio format and audio line compatibility.
     *
     * @throws UnsupportedOperationException if the audio format is not supported
     * @throws IllegalStateException if the audio line cannot be opened
     */
    public JavaMicRecorder() {
        this.isRecording = false;

        // check if the audio line is compatible
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) throw new RuntimeException("Audio Format not supported");

        // audio line compatible: open audio line
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException ex) {
            throw new IllegalStateException("Failed to open audio line.");
        }
    }

    /**
     * Start recording by starting the line,
     * and open a thread that will end as the recording ends.
     */
    @Override
    public void start() {
        if (isRecording) return;

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
        } catch (InterruptedException e) {
            System.out.println("Recording thread interrupted");
        }

        System.out.println("Recording ended.");
        System.out.println("Byte size: " + recordingByteData.size());
        System.out.println("Recording length: " + (recordingByteData.size() / (44100.0 * 2 * 2)) + " seconds");
    }

    @Override
    public boolean isRecording() { return this.isRecording; }

    @Override
    public byte[] getRecordingBytes() {
        return recordingByteData.toByteArray();
    }

    public AudioFormat getAudioFormat() { return format; }

    /**
     * Create a thread that will be active as long as isRecording is true.
     * At termination, line is closed.
     */
    private void startRecordingThread() {
        line.start();
        recordingThread = new Thread(() -> {
            int numBytesRead = 0;
            byte[] data = new byte[line.getBufferSize() / 5];
            while (isRecording) {
                // read the next chunk of data from line
                numBytesRead = line.read(data, 0, data.length);
                recordingByteData.write(data, 0, numBytesRead);
            }

            line.stop();
        });
        recordingThread.start();

        System.out.println("Starting to record...");
    }
}
