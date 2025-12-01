package org.wavelabs.soundscope.use_case.save_recording;

public interface SaveRecordingOB {
    /**
     * Saving recording complete.
     * Present the according view.
     */
    void presentSaveSuccessView();

    /**
     * Error during save recording.
     * Present the according view.
     *
     * @param message String message including the details of the error
     */
    void presentError(String message);
}
