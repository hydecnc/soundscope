package org.wavelabs.soundscope.usecase;

/**
 * Output boundary interface for ProcessAudioFileUseCase.
 * Defines the contract for output from the use case.
 * Implemented by the Presenter.
 */
public interface ProcessAudioFileOutputBoundary {
    /**
     * Receives the output data from the use case.
     * 
     * @param outputData The output data containing processed audio information
     */
    void present(ProcessAudioFileOutputData outputData);
    
    /**
     * Receives an error from the use case.
     * 
     * @param errorMessage The error message
     * @param fileName The name of the file that caused the error
     */
    void presentError(String errorMessage, String fileName);
}

