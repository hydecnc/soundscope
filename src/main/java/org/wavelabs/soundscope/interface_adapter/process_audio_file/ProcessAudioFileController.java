package org.wavelabs.soundscope.interface_adapter.process_audio_file;

import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileIB;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileID;

import java.io.File;

public class ProcessAudioFileController {
    private final ProcessAudioFileIB  processAudioFileInteractor;

    public ProcessAudioFileController(ProcessAudioFileIB processAudioFile) {
        this.processAudioFileInteractor = processAudioFile;
    }

    public void execute(File file) {
        ProcessAudioFileID inputData = new ProcessAudioFileID(file);
        processAudioFileInteractor.execute(inputData);
    }
}
