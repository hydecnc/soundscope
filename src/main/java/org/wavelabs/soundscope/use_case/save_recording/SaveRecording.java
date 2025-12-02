package org.wavelabs.soundscope.use_case.save_recording;

import java.io.IOException;

<<<<<<< recording-test-case
=======
import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.FileSaver;

>>>>>>> main
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

        // check if audio recording is null for preventing NPE
        if (!saveRecordingDAO.hasAudioRecording()) {
            saveRecordingPresenter.presentError("Recording is empty");
            return;
        }

        try {
            boolean success = saveRecordingDAO.saveToFile(filePath);
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
