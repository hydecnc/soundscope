package org.wavelabs.soundscope.usecase;

/**
 * Input boundary interface for ProcessAudioFileUseCase.
 * Defines the contract for input to the use case.
 * Implemented by the Use Case Interactor.
 */
public interface ProcessAudioFileInputBoundary {
    /**
     * Executes the use case to process an audio file.
     * 
     * @param inputData The input data containing the file to process
     */
    void execute(ProcessAudioFileInputData inputData);
}

