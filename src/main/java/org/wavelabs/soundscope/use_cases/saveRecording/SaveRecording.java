package org.wavelabs.soundscope.use_cases.saveRecording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.FileSaver;

import java.io.IOException;

public class SaveRecording implements SaveRecordingIB {
    private final SaveRecordingDAI saveRecordingDAO;
    private final SaveRecordingOB saveRecordingPresenter;

    public SaveRecording (SaveRecordingDAI saveRecordingDAI, SaveRecordingOB saveRecordingOB) {
        this.saveRecordingDAO = saveRecordingDAI;
        this.saveRecordingPresenter = saveRecordingOB;
    }

    @Override
    public void execute(SaveRecordingID inputData) {
        final String filePath = inputData.getFilePath();
        final AudioRecording audioRecording = saveRecordingDAO.getAudioRecording();

        final FileSaver fileSaver = saveRecordingDAO.getFileSaver();
        boolean success;
        try {
            success = fileSaver.save(filePath, audioRecording);
        } catch (IOException ex) {
            success = false;
        }

        // TODO: show error according to success
        saveRecordingPresenter.presentSaveSuccessView(); // end use case
    }
}
