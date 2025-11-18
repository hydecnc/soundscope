package org.wavelabs.soundscope.use_case.process_audio_file;

/**
 * Output boundary interface for ProcessAudioFileUseCase.
 * Defines the contract for output from the use case.
 * Implemented by the Presenter.
 */
public interface ProcessAudioFileOB {
    /**
     * Receives the output data from the use case.
     * 
     * @param outputData The output data containing processed audio information
     */
    void present(ProcessAudioFileOD outputData);
    
    /**
     * Receives an error from the use case.
     * 
     * @param errorMessage The error message
     * @param fileName The name of the file that caused the error
     */
    void presentError(String errorMessage, String fileName);
}


