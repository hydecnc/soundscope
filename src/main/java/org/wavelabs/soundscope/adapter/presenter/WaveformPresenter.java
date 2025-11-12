package org.wavelabs.soundscope.adapter.presenter;

import org.wavelabs.soundscope.adapter.viewmodel.WaveformViewModel;
import org.wavelabs.soundscope.domain.AudioData;
import org.wavelabs.soundscope.usecase.ProcessAudioFileOutputBoundary;
import org.wavelabs.soundscope.usecase.ProcessAudioFileOutputData;

/**
 * Presenter for waveform visualization.
 * Implements the Output Boundary interface.
 * Receives output data from the use case and updates the View Model.
 */
public class WaveformPresenter implements ProcessAudioFileOutputBoundary {
    private final WaveformViewModel viewModel;
    
    public WaveformPresenter(WaveformViewModel viewModel) {
        this.viewModel = viewModel;
    }
    
    @Override
    public void present(ProcessAudioFileOutputData outputData) {
        viewModel.setAudioData(outputData.getAudioData());
        
        String metadata = formatAudioMetadata(outputData.getAudioData());
        viewModel.setOutputText(
            "File loaded: " + outputData.getFileName() + "<br>" + metadata
        );
    }
    
    @Override
    public void presentError(String errorMessage, String fileName) {
        String formattedError = formatError(errorMessage, fileName);
        viewModel.setOutputText(formattedError);
    }
    
    private String formatAudioMetadata(AudioData audioData) {
        if (audioData == null) {
            return "";
        }
        
        String duration = String.format("%.2f", audioData.getDurationSeconds());
        return String.format(
            "Duration: %ss | Sample Rate: %d Hz | Channels: %d",
            duration,
            audioData.getSampleRate(),
            audioData.getChannels()
        );
    }
    
    private String formatError(String errorMessage, String fileName) {
        if (errorMessage.contains("Unsupported")) {
            return "Unsupported audio format.<br>" +
                   "Please use MP3 format only.<br>" +
                   "File: " + fileName;
        } else if (errorMessage.contains("corrupted")) {
            return "File appears to be corrupted or cannot be read.<br>" +
                   "Please try a different file or re-record.";
        } else {
            return "Error: " + errorMessage + "<br>File: " + fileName;
        }
    }
}

