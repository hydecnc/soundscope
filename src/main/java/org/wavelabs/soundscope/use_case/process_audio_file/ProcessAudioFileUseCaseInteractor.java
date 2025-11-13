package org.wavelabs.soundscope.use_case.process_audio_file;

import org.wavelabs.soundscope.entity.AudioData;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Use Case Interactor for processing audio files.
 * 
 * <p>Contains the application-specific business logic for the ProcessAudioFile use case.
 * This class is part of the Application Business Rules layer and implements the
 * ProcessAudioFileInputBoundary interface.
 * 
 * <p>This interactor coordinates the processing of audio files by:
 * <ul>
 *   <li>Using the AudioFileGateway to read and process the audio file</li>
 *   <li>Creating Output Data with the processed audio information</li>
 *   <li>Presenting the results through the Output Boundary</li>
 *   <li>Handling errors and presenting them through the Output Boundary</li>
 * </ul>
 */
public class ProcessAudioFileUseCaseInteractor implements ProcessAudioFileInputBoundary {
    private final AudioFileGateway audioFileGateway;
    private final ProcessAudioFileOutputBoundary outputBoundary;
    
    /**
     * Constructs a ProcessAudioFileUseCaseInteractor with the specified dependencies.
     * 
     * @param audioFileGateway The gateway for reading and processing audio files
     * @param outputBoundary The output boundary for presenting results and errors
     */
    public ProcessAudioFileUseCaseInteractor(
            AudioFileGateway audioFileGateway,
            ProcessAudioFileOutputBoundary outputBoundary) {
        this.audioFileGateway = audioFileGateway;
        this.outputBoundary = outputBoundary;
    }
    
    /**
     * Executes the ProcessAudioFile use case.
     * 
     * <p>Processes the audio file specified in the input data, extracts amplitude
     * samples, and presents the results through the output boundary. If an error
     * occurs, it is caught and presented as an error through the output boundary.
     * 
     * @param inputData The input data containing the file to process
     */
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

