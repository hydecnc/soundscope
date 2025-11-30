package org.wavelabs.soundscope.interface_adapter.process_audio_file;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.wavelabs.soundscope.data_access.FileDAO;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileIB;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileID;

import java.io.File;
import java.io.IOException;

public class ProcessAudioFileController {
    private final ProcessAudioFileIB processAudioFileInteractor;
    private final FileDAO fileDAO;

    public ProcessAudioFileController(ProcessAudioFileIB processAudioFile, FileDAO fileDAO) {
        this.processAudioFileInteractor = processAudioFile;
        this.fileDAO = fileDAO;
    }

    public void execute(File file) {
        ProcessAudioFileID inputData = new ProcessAudioFileID(file);
        processAudioFileInteractor.execute(inputData);

        try {
            fileDAO.loadAudioFromFile(file);
        } catch (IOException | UnsupportedAudioFileException e) {
            System.err.println("Warning: Failed to load audio for fingerprinting: " + e.getMessage());
        }
    }
}
