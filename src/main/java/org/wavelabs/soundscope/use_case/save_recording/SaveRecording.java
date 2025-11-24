package org.wavelabs.soundscope.use_case.save_recording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.FileSaver;

import java.io.IOException;

public class SaveRecording implements SaveRecordingIB {
    private final SaveRecordingDAI saveRecordingDAO;
    private final SaveRecordingOB saveRecordingPresenter;

    public SaveRecording(SaveRecordingDAI saveRecordingDAI, SaveRecordingOB saveRecordingOB) {
        this.saveRecordingDAO = saveRecordingDAI;
        this.saveRecordingPresenter = saveRecordingOB;
    }

    @Override
    public void execute(SaveRecordingID inputData) {
        final String filePath = inputData.getFilePath();
        final AudioRecording audioRecording = saveRecordingDAO.getAudioRecording();

        final FileSaver fileSaver = saveRecordingDAO.getFileSaver();
        try {
            fileSaver.save(filePath, audioRecording);
        } catch (IOException ex) {
            // TODO: appropriate error handling
        }

        // TODO: show error message to user if save failed
        saveRecordingPresenter.presentSaveSuccessView(); // end use case
    }
}
