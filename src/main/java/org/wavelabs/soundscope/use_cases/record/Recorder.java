package org.wavelabs.soundscope.use_cases.record;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Records audio input from the system's default microphone. Supports WAV format with configurable
 * sample rates.
 */
public class Recorder {
    private volatile boolean isRecording;
    private TargetDataLine line;
    private Thread recordingThread;
    private ByteArrayOutputStream recordedData;
    private final AudioFormat format = new AudioFormat(44100.0f, 16, 2, true, false);

    /**
     * Crates a new Recorder with default audio format settings.
     *
     * @throws UnsupportedOperationException if the audio format is not supported
     * @throws IllegalStateException if the audio line cannot be opened
     */
    public Recorder() {
        isRecording = false;
        recordedData = new ByteArrayOutputStream();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            throw new UnsupportedOperationException("Audio format not supported");
        }
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException ex) {
            throw new IllegalStateException("Failed to open Audio Line.", ex);
        }
    }

    /**
     * Starts recording audio from the microphone.
     */
    public void start() {
        if (isRecording)
            return;

        isRecording = true;
        line.start();

        // Create and start the recording thread
        recordingThread = new Thread(() -> {
            int numBytesRead;
            byte[] data = new byte[line.getBufferSize() / 5];
            while (isRecording) {
                // Read the next chunk of data from TargetDataLine
                numBytesRead = line.read(data, 0, data.length);
                recordedData.write(data, 0, numBytesRead);
                // TODO: Process this chunk of data
            }

            line.stop();
        });
        System.out.println("Starting to record...");
        recordingThread.start();
    }

    /**
     * Stop recording audio from the microphone.
     */
    public void stop() {
        isRecording = false;

        try {
            recordingThread.join();
        } catch (InterruptedException ex) {

        }

        System.out.println("Recorded " + recordedData.size() + " bytes");
        System.out.println(
                "Which is around " + (recordedData.size() / (44100.0 * 2 * 2)) + " seconds");
    }

    /**
     * Save currently stored recordedData as a WAV file to the given filepath.
     *
     * @param filePath the name of the file to save to
     */
    public void saveToFile(String filePath) {
        if (isRecording)
            return;

        try {
            byte[] audioData = recordedData.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream ais =
                    new AudioInputStream(bais, format, audioData.length / format.getFrameSize());
            File fileOut = new File(filePath);
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, fileOut);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }
}
