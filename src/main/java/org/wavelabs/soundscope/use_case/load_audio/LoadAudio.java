package org.wavelabs.soundscope.use_case.load_audio;

import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileOB;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileOD;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class LoadAudio implements LoadAudioIB {
    private final LoadAudioDAI loadAudioGateway;
    private final ProcessAudioFileOB outputBoundary;

    public LoadAudio(LoadAudioDAI loadAudioGateway, ProcessAudioFileOB outputBoundary) {
        this.loadAudioGateway = loadAudioGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(LoadAudioID audioSource) {
        try {
            File file = audioSource.getFile();
            AudioData audioData = loadAudioGateway.processAudioFile(file);
            ProcessAudioFileOD outputData = new ProcessAudioFileOD(audioData, file.getName());
            outputBoundary.present(outputData);

        } catch (UnsupportedAudioFileException e) {
            outputBoundary.presentError("Unsupported audio format: ", audioSource.getFile().getName());
        } catch (IOException e) {
            outputBoundary.presentError("File appears to be corrupted or cannot be read: ", audioSource.getFile().getName());
        } catch (Exception e) {
            outputBoundary.presentError("An unexpected error occurred with this file: ", audioSource.getFile().getName());
        }
    }
}
