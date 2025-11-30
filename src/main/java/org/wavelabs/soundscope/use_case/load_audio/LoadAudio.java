package org.wavelabs.soundscope.use_case.load_audio;

import org.wavelabs.soundscope.entity.AudioRecording;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class LoadAudio implements LoadAudioIB {
    private final LoadAudioDAI loadAudioGateway;
    private final LoadAudioOB outputBoundary;

    public LoadAudio(LoadAudioDAI loadAudioGateway, LoadAudioOB outputBoundary) {
        this.loadAudioGateway = loadAudioGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(LoadAudioID audioSource) {
        try {
            File file = audioSource.getFile();
            AudioRecording audioData = loadAudioGateway.loadAudio(file);
            LoadAudioOD outputData = new LoadAudioOD(audioData, file.getPath());
            outputBoundary.presentSuccess(outputData);

        } catch (UnsupportedAudioFileException e) {
            outputBoundary.presentFailure("Unsupported audio format: " + audioSource.getFile().getName());
        } catch (IOException e) {
            outputBoundary.presentFailure("File appears to be corrupted or cannot be read: " + audioSource.getFile().getName());
        } catch (Exception e) {
            outputBoundary.presentFailure("An unexpected error occurred with this file: " + audioSource.getFile().getName());
        }
    }
}
