package org.wavelabs.soundscope.use_case.save_audio;

import org.junit.Assert;
import org.junit.Test;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecording;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingDAI;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingID;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingOB;

import java.io.IOException;

public class SaveAudioTest {
    @Test
    public void successfulSaveAudioExecute() {
        MockSaveAudioDAO mockDAO = new MockSaveAudioDAO(true);
        MockSaveAudioOB mockOB = new MockSaveAudioOB();

        SaveRecording recordingTest = new SaveRecording(mockDAO, mockOB);
        SaveRecordingID mockInputData =  new SaveRecordingID("audiofile.wav");

        recordingTest.execute(mockInputData);
        Assert.assertTrue(mockDAO.saveFileSuccess);
    }

    @Test
    public void failedSaveAudioExecute() {
        MockSaveAudioDAO mockDAO = new MockSaveAudioDAO(true);
        MockSaveAudioOB mockOB = new MockSaveAudioOB();

        SaveRecording recordingTest = new SaveRecording(mockDAO, mockOB);
        SaveRecordingID mockInputData =  new SaveRecordingID("badaudio.wav");

        recordingTest.execute(mockInputData);
        Assert.assertFalse(mockDAO.saveFileSuccess);
    }

    @Test
    public void noSuccessExecute() {
        MockSaveAudioDAO mockDAO = new MockSaveAudioDAO(true);
        MockSaveAudioOB mockOB = new MockSaveAudioOB();

        SaveRecording recordingTest = new SaveRecording(mockDAO, mockOB);
        SaveRecordingID mockInputData =  new SaveRecordingID("nosave.wav");

        recordingTest.execute(mockInputData);
        Assert.assertFalse(mockDAO.saveFileSuccess);
    }

    @Test
    public void noAudioRecordingExecute() {
        MockSaveAudioDAO mockDAO = new MockSaveAudioDAO(false);
        MockSaveAudioOB mockOB = new MockSaveAudioOB();

        SaveRecording recordingTest = new SaveRecording(mockDAO, mockOB);
        SaveRecordingID mockInputData =  new SaveRecordingID("audiofile.wav");

        recordingTest.execute(mockInputData);
        Assert.assertFalse(mockDAO.saveFileSuccess);
    }

}

class MockSaveAudioDAO implements SaveRecordingDAI {
    boolean saveFileSuccess;
    boolean hasAudioRecording;

    public MockSaveAudioDAO(boolean hasAudioRecording) {
        this.saveFileSuccess = false;
        this.hasAudioRecording = hasAudioRecording;
    }

    @Override
    public boolean saveToFile(String filePath) throws IOException {
        if (filePath.equals("badaudio.wav")) {
            throw new IOException("IO exception");
        }
        if (filePath.equals("nosave.wav")) {
            return false;
        }
        saveFileSuccess = true;
        return true;
    }

    @Override
    public boolean hasAudioRecording() {
        return hasAudioRecording;
    }
}

class MockSaveAudioOB implements SaveRecordingOB {
    @Override
    public void presentSaveSuccessView() {}

    @Override
    public void presentError(String message) {System.out.println("error found");}
}

