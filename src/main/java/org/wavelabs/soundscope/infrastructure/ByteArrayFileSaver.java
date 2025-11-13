package org.wavelabs.soundscope.infrastructure;

import org.wavelabs.soundscope.entity.AudioRecording;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class ByteArrayFileSaver implements FileSaver {
    /**
     *
     * @param filePath path to save the data
     * @return true iff save was success
     */
    public boolean save(String filePath, AudioRecording audioRecording) throws IOException {
        byte[] audioData = audioRecording.getData();
        AudioFormat format = audioRecording.getFormat();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream ais =
                    new AudioInputStream(bais, format, audioData.length / format.getFrameSize());
            File fileOut = new File(filePath);
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, fileOut);
            System.out.println("File saved to: " + fileOut.getAbsolutePath());
        } catch (Exception ex) {
            System.out.println("Error saving file: " + ex.getMessage());
            return false;
        }
        return true;
    }
}
