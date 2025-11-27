package org.wavelabs.soundscope.interface_adapter.save_file;

import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingIB;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingID;

public class SaveRecordingController {
    public final SaveRecordingIB saveRecordingInteractor;

    public SaveRecordingController(SaveRecordingIB saveRecordingInteractor) {
        this.saveRecordingInteractor = saveRecordingInteractor;
    }

    public void execute(String absolutePath){
        saveRecordingInteractor.execute(new SaveRecordingID(absolutePath));
    }
}
