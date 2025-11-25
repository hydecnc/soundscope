package org.wavelabs.soundscope.use_case.display_recording_waveform;

/**
 * Input boundary interface for DisplayRecordingWaveform use case.
 * Defines the contract for input to the use case.
 * Implemented by the Use Case Interactor.
 */
public interface DisplayRecordingWaveformIB {
    /**
     * Executes the use case to display the waveform of the current recording.
     * 
     * @param inputData The input data (may be empty if no input is needed)
     */
    void execute(DisplayRecordingWaveformID inputData);
}

