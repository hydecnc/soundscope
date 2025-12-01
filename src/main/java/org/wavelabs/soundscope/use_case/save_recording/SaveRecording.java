package org.wavelabs.soundscope.use_case.save_recording;

import java.io.IOException;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.FileSaver;

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

        // check if audio recording is null for preventing NPE
        if (audioRecording == null) {
            saveRecordingPresenter.presentError("Recording is empty");
            return;
        }

        try {
            final boolean success = fileSaver.save(filePath, audioRecording);
            if (!success) {
                saveRecordingPresenter.presentError("Save failed");
            }
        }
        catch (IOException ex) {
            saveRecordingPresenter.presentError("IO error while saving");
        }
        saveRecordingPresenter.presentSaveSuccessView();
        // end use case
    }
}
