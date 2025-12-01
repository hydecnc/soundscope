package org.wavelabs.soundscope.interface_adapter.process_audio_file;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.wavelabs.soundscope.data_access.FileDAO;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileIB;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileID;

public class ProcessAudioFileController {
    private final ProcessAudioFileIB processAudioFileInteractor;
    private final FileDAO fileDAO;

    public ProcessAudioFileController(ProcessAudioFileIB processAudioFile, FileDAO fileDAO) {
        this.processAudioFileInteractor = processAudioFile;
        this.fileDAO = fileDAO;
    }

    /**
     * Executes the audio file processing workflow for the specified file.
     *
     * <p>This method constructs a {@link ProcessAudioFileID} input object and
     * passes it to the {@code processAudioFileInteractor}, which performs the
     * domain-level processing of the audio file (e.g., decoding, validation, or
     * preparing data for further operations such as fingerprinting).</p>
     *
     * <p>After delegating to the use case interactor, the method attempts to load
     * the raw audio data from the file through the {@code fileDAO}. This step is
     * typically required to make audio data available for subsequent operations
     * (such as playback or fingerprint generation). If loading fails, a warning is
     * logged but the exception is not propagated, allowing the system to continue
     * running in a degraded state.</p>
     *
     * @param file
     *         the audio file to be processed; must not be {@code null}
     */
    public void execute(File file) {
        final ProcessAudioFileID inputData = new ProcessAudioFileID(file);
        processAudioFileInteractor.execute(inputData);

        try {
            fileDAO.loadAudioFromFile(file);
        }
        catch (IOException | UnsupportedAudioFileException event) {
            System.err.println("Warning: Failed to load audio for fingerprinting: " + e.getMessage());
        }
    }
}
