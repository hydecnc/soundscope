package org.wavelabs.soundscope.use_case.save_recording;

public interface SaveRecordingIB {
    /**
     * Executes the save recording use case.
     * @param inputData input data object containing the file path to save recording
     */
    void execute(SaveRecordingID inputData);
}
