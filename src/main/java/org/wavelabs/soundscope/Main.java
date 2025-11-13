package org.wavelabs.soundscope;

import org.wavelabs.soundscope.data_access.FileDAO;
import org.wavelabs.soundscope.infrastructure.ByteArrayFileSaver;
import org.wavelabs.soundscope.infrastructure.JavaMicRecorder;
import org.wavelabs.soundscope.interface_adapter.DummyPresenter;
import org.wavelabs.soundscope.use_cases.saveRecording.SaveRecording;
import org.wavelabs.soundscope.use_cases.saveRecording.SaveRecordingID;
import org.wavelabs.soundscope.use_cases.startRecording.StartRecording;
import org.wavelabs.soundscope.use_cases.stopRecording.StopRecording;

public class Main {
    public static void main(String[] args) {
        System.out.println("Record");

        FileDAO fileDAO = new FileDAO();
        fileDAO.setFileSaver(new ByteArrayFileSaver());
        fileDAO.setRecorder(new JavaMicRecorder());

        DummyPresenter dummyPresenter = new DummyPresenter();

        StartRecording startRecording = new StartRecording(fileDAO, dummyPresenter);
        StopRecording stopRecording = new StopRecording(fileDAO, dummyPresenter);
        SaveRecording saveRecording = new SaveRecording(fileDAO, dummyPresenter);

        startRecording.execute();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopRecording.execute();
        // TODO: your home directory
        saveRecording.execute(new SaveRecordingID("/Users/YourHomeDirectory/Desktop/output.wav"));
        System.out.println("Record End");

    }
}
