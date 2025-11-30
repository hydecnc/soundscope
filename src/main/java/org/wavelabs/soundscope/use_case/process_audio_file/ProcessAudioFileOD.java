package org.wavelabs.soundscope.use_case.process_audio_file;

import org.wavelabs.soundscope.entity.AudioData;

/**
 * Output data structure for ProcessAudioFileUseCase.
 *
 * <p>Represents the data passed from the Use Case Interactor to the Presenter.
 * Contains the processed audio data and the original file name for display purposes.
 *
 * <p>This class is part of the Application Business Rules layer and follows
 * Clean Architecture principles by using domain entities (AudioData).
 */
public class ProcessAudioFileOD {
    private final AudioData audioData;
    private final String fileName;

    /**
     * Constructs a ProcessAudioFileOD with the specified audio data and file name.
     *
     * @param audioData The processed audio data containing amplitude samples and metadata
     * @param fileName  The name of the original audio file
     */
    public ProcessAudioFileOD(AudioData audioData, String fileName) {
        this.audioData = audioData;
        this.fileName = fileName;
    }

    /**
     * Gets the processed audio data.
     *
     * @return The AudioData object containing amplitude samples and metadata
     */
    public AudioData getAudioData() {
        return audioData;
    }

    /**
     * Gets the original file name.
     *
     * @return The name of the original audio file
     */
    public String getFileName() {
        return fileName;
    }
}


