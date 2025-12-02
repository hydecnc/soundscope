package org.wavelabs.soundscope.use_case.load_audio;

import org.junit.Assert;
import org.junit.Test;
import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.use_case.process_audio_file.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class ProcessAudioFileTest {
    @Test
    public void successfulLoadAudioFile() {
        MockProcessAudioFileDAO mockDAO = new MockProcessAudioFileDAO();
        MockProcessAudioFileOB mockOB = new MockProcessAudioFileOB();

        File mockFile = new File("audiofile.wav");
        ProcessAudioFileID mockInputData = new ProcessAudioFileID(mockFile);

        ProcessAudioFile tester  = new ProcessAudioFile(mockDAO, mockOB);
        tester.execute(mockInputData);

        Assert.assertTrue(mockDAO.success);
    }

    @Test
    public void UnsupportedAudioFileTest() {
        MockProcessAudioFileDAO mockDAO = new MockProcessAudioFileDAO();
        MockProcessAudioFileOB mockOB = new MockProcessAudioFileOB();

        File mockFile = new File("audiofile.mp3");
        ProcessAudioFileID mockInputData = new ProcessAudioFileID(mockFile);

        ProcessAudioFile tester  = new ProcessAudioFile(mockDAO, mockOB);
        tester.execute(mockInputData);

        Assert.assertFalse(mockDAO.success);
    }

    @Test
    public void IOBadFileTest() {
        MockProcessAudioFileDAO mockDAO = new MockProcessAudioFileDAO();
        MockProcessAudioFileOB mockOB = new MockProcessAudioFileOB();

        File mockFile = new File("badfile.wav");
        ProcessAudioFileID mockInputData = new ProcessAudioFileID(mockFile);

        ProcessAudioFile tester  = new ProcessAudioFile(mockDAO, mockOB);
        tester.execute(mockInputData);

        Assert.assertFalse(mockDAO.success);
    }
}

class MockProcessAudioFileDAO implements ProcessAudioFileDAI {
    boolean success;

    @Override
    public AudioData processAudioFile(File file) throws UnsupportedAudioFileException, IOException {
        if (file.getName().endsWith(".mp3")) {
            throw new UnsupportedAudioFileException("bad file extension");
        }
        if (file.getName().equals("badfile.wav")) {
            throw new IOException("file cannot be read");
        }
        success = true;
        return null;
    }
}

class MockProcessAudioFileOB implements ProcessAudioFileOB {
    @Override
    public void present(ProcessAudioFileOD outputData) {}

    @Override
    public void presentError(String errorMessage, String fileName) {}
}

