package org.wavelabs.soundscope.use_case.display_recording_waveform;

/**
 * Output boundary interface for DisplayRecordingWaveform use case.
 * Defines the contract for output from the use case.
 * Implemented by the Presenter.
 */
public interface DisplayRecordingWaveformOB {
    /**
     * Receives the output data from the use case.
     * 
     * @param outputData The output data containing processed audio information for display
     */
    void present(DisplayRecordingWaveformOD outputData);
    
    /**
     * Receives an error from the use case.
     * 
     * @param errorMessage The error message
     */
    void presentError(String errorMessage);
}

