package org.wavelabs.soundscope.infrastructure;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.wavelabs.soundscope.entity.AudioRecording;


/**
 * Actual Implementation of FileSaver in ByteArray data.
 */
public class ByteArrayFileSaver implements FileSaver {
    /**
     * Saves audio file from audio recording.
     * @param filePath       path to save the data
     * @param audioRecording the data to be saved at {@code filePath}
     * @return true iff save was success
     * @throws IOException if errored during saving process
     */
    @Override
    public boolean save(String filePath, AudioRecording audioRecording) throws IOException {
        final byte[] audioData = audioRecording.getData();
        final AudioFormat format = audioRecording.getFormat();
        boolean returnStatus = true;
        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            final AudioInputStream ais =
                new AudioInputStream(bais, format, audioData.length / format.getFrameSize());
            final File fileOut = new File(filePath);
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, fileOut);
            System.out.println("File saved to: " + fileOut.getAbsolutePath());
        }
        catch (IOException ex) {
            System.out.println("Error saving file: " + ex.getMessage());
            returnStatus = false;
        }
        return returnStatus;
    }
}
