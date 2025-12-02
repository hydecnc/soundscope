package org.wavelabs.soundscope.use_case.start_recording;

public interface RecordingOB {
    void updateRecordingState(RecordingOD outputData);

    /**
     * Error during save recording.
     * Present the according view.
     * @param message String message including the details of the error
     */
    void presentError(String message);
}
