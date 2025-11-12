package org.wavelabs.soundscope.usecase;

import java.io.File;

/**
 * Input data structure for ProcessAudioFileUseCase.
 * Represents the data passed from Controller to Use Case.
 */
public class ProcessAudioFileInputData {
    private final File file;
    
    public ProcessAudioFileInputData(File file) {
        this.file = file;
    }
    
    public File getFile() {
        return file;
    }
}

