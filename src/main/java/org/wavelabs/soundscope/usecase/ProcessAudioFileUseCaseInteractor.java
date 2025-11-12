package org.wavelabs.soundscope.usecase;

import org.wavelabs.soundscope.domain.AudioData;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Use Case Interactor for processing audio files.
 * Contains the application-specific business logic.
 * Implements the Input Boundary interface.
 */
public class ProcessAudioFileUseCaseInteractor implements ProcessAudioFileInputBoundary {
    private final AudioFileGateway audioFileGateway;
    private final ProcessAudioFileOutputBoundary outputBoundary;
    
    public ProcessAudioFileUseCaseInteractor(
            AudioFileGateway audioFileGateway,
            ProcessAudioFileOutputBoundary outputBoundary) {
        this.audioFileGateway = audioFileGateway;
        this.outputBoundary = outputBoundary;
    }
    
    @Override
    public void execute(ProcessAudioFileInputData inputData) {
        try {
            File file = inputData.getFile();
            AudioData audioData = audioFileGateway.processAudioFile(file);
            
            ProcessAudioFileOutputData outputData = new ProcessAudioFileOutputData(
                audioData,
                file.getName()
            );
            
            outputBoundary.present(outputData);
            
        } catch (UnsupportedAudioFileException e) {
            outputBoundary.presentError("Unsupported audio format", inputData.getFile().getName());
        } catch (IOException e) {
            outputBoundary.presentError("File appears to be corrupted or cannot be read", 
                                       inputData.getFile().getName());
        } catch (Exception e) {
            outputBoundary.presentError("An unexpected error occurred: " + e.getMessage(),
                                       inputData.getFile().getName());
        }
    }
}

