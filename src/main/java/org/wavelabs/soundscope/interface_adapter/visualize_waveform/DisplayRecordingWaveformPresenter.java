package org.wavelabs.soundscope.interface_adapter.visualize_waveform;

import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformOB;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformOD;

/**
 * Presenter for displaying waveform during recording use case.
 * 
 * <p>Implements the DisplayRecordingWaveformOB interface, receiving output data
 * from the use case interactor and updating the ViewModel for real-time waveform display.
 * 
 * <p>This class is part of the Interface Adapters layer and is responsible for:
 * <ul>
 *   <li>Receiving output data from the DisplayRecordingWaveform use case</li>
 *   <li>Updating the ViewModel with real-time audio data</li>
 *   <li>Handling and formatting error messages</li>
 * </ul>
 */
public class DisplayRecordingWaveformPresenter implements DisplayRecordingWaveformOB {
    private final WaveformViewModel viewModel;
    
    /**
     * Constructs a DisplayRecordingWaveformPresenter with the specified ViewModel.
     * 
     * @param viewModel The ViewModel to update with real-time audio data
     */
    public DisplayRecordingWaveformPresenter(WaveformViewModel viewModel) {
        this.viewModel = viewModel;
    }
    
    /**
     * Presents the current recording buffer for waveform display.
     * 
     * <p>Updates the ViewModel with the real-time audio data from the recording buffer.
     * 
     * @param outputData The output data containing processed audio information from the recording buffer
     */
    @Override
    public void present(DisplayRecordingWaveformOD outputData) {
        viewModel.setAudioData(outputData.getAudioData());
        viewModel.setOutputText("Recording...");
    }
    
    /**
     * Presents an error that occurred during recording waveform display.
     * 
     * <p>Formats the error message appropriately and updates the ViewModel
     * to display the error to the user.
     * 
     * @param errorMessage The error message describing what went wrong
     */
    @Override
    public void presentError(String errorMessage) {
        viewModel.setOutputText("Error: " + errorMessage);
    }
}

