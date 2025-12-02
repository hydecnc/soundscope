package org.wavelabs.soundscope.use_case.process_audio_file;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.wavelabs.soundscope.entity.AudioData;

/**
 * Use Case Interactor for processing audio files.
 *
 * <p>Contains the application-specific business logic for the ProcessAudioFile use case.
 * This class is part of the Application Business Rules layer and implements the
 * ProcessAudioFileIB interface.
 *
 * <p>This interactor coordinates the processing of audio files by:
 * <ul>
 *   <li>Using the ProcessAudioFileDAI to read and process the audio file</li>
 *   <li>Creating Output Data with the processed audio information</li>
 *   <li>Presenting the results through the Output Boundary</li>
 *   <li>Handling errors and presenting them through the Output Boundary</li>
 * </ul>
 */
public class ProcessAudioFile implements ProcessAudioFileIB {
    private final ProcessAudioFileDAI processAudioFileDAO;
    private final ProcessAudioFileOB outputBoundary;

    /**
     * Constructs a ProcessAudioFile with the specified dependencies.
     *
     * @param processAudioFileDAO The data access interface for reading and processing audio files
     * @param outputBoundary      The output boundary for presenting results and errors
     */
    public ProcessAudioFile(
        ProcessAudioFileDAI processAudioFileDAO,
        ProcessAudioFileOB outputBoundary) {
        this.processAudioFileDAO = processAudioFileDAO;
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
    public void execute(ProcessAudioFileID inputData) {
        try {
            final File file = inputData.getFile();
            final AudioData audioData = processAudioFileDAO.processAudioFile(file);

            final ProcessAudioFileOD outputData = new ProcessAudioFileOD(
                audioData,
                file.getName()
            );

            outputBoundary.present(outputData);

        }
        catch (UnsupportedAudioFileException exception) {
            outputBoundary.presentError("Unsupported audio format", inputData.getFile().getName());
        }
        catch (IOException exception) {
            outputBoundary.presentError("File appears to be corrupted or cannot be read",
                inputData.getFile().getName());
        }
    }
}
