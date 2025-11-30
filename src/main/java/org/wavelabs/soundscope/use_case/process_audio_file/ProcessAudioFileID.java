package org.wavelabs.soundscope.use_case.process_audio_file;

import java.io.File;

/**
 * Input data structure for ProcessAudioFileUseCase.
 *
 * <p>Represents the data passed from the Controller to the Use Case Interactor.
 * This is a simple data transfer object containing the file to be processed.
 *
 * <p>This class is part of the Application Business Rules layer and follows
 * Clean Architecture principles by using only standard Java types and domain entities.
 */
public class ProcessAudioFileID {
    private final File file;

    /**
     * Constructs a ProcessAudioFileID with the specified file.
     *
     * @param file The audio file to process
     */
    public ProcessAudioFileID(File file) {
        this.file = file;
    }

    /**
     * Gets the file to be processed.
     *
     * @return The audio file to process
     */
    public File getFile() {
        return file;
    }
}


