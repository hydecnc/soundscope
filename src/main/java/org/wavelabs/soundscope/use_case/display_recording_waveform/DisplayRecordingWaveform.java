package org.wavelabs.soundscope.use_case.display_recording_waveform;

import org.wavelabs.soundscope.entity.AudioData;

/**
 * Use Case Interactor for displaying waveform during recording.
 * 
 * <p>Contains the application-specific business logic for the DisplayRecordingWaveform use case.
 * This class is part of the Application Business Rules layer and implements the
 * DisplayRecordingWaveformIB interface.
 * 
 * <p>This interactor coordinates the display of waveform during recording by:
 * <ul>
 *   <li>Using the DisplayRecordingWaveformDAI to get the current recording buffer</li>
 *   <li>Creating Output Data with the processed audio information</li>
 *   <li>Presenting the results through the Output Boundary</li>
 *   <li>Handling errors and presenting them through the Output Boundary</li>
 * </ul>
 */
public class DisplayRecordingWaveform implements DisplayRecordingWaveformIB {
    private final DisplayRecordingWaveformDAI displayRecordingWaveformDAO;
    private final DisplayRecordingWaveformOB outputBoundary;
    
    /**
     * Constructs a DisplayRecordingWaveform with the specified dependencies.
     * 
     * @param displayRecordingWaveformDAO The data access interface for getting current recording buffer
     * @param outputBoundary The output boundary for presenting results and errors
     */
    public DisplayRecordingWaveform(
            DisplayRecordingWaveformDAI displayRecordingWaveformDAO,
            DisplayRecordingWaveformOB outputBoundary) {
        this.displayRecordingWaveformDAO = displayRecordingWaveformDAO;
        this.outputBoundary = outputBoundary;
    }
    
    /**
     * Executes the DisplayRecordingWaveform use case.
     * 
     * <p>Gets the current recording buffer, processes it to extract amplitude
     * samples, and presents the results through the output boundary. If an error
     * occurs or recording is not active, it is handled appropriately.
     * 
     * @param inputData The input data (may be empty)
     */
    @Override
    public void execute(DisplayRecordingWaveformID inputData) {
        try {
            AudioData audioData = displayRecordingWaveformDAO.getCurrentRecordingBuffer();
            
            if (audioData == null) {
                // Not currently recording, no error needed
                return;
            }
            
            DisplayRecordingWaveformOD outputData = new DisplayRecordingWaveformOD(audioData);
            outputBoundary.present(outputData);
            
        } catch (Exception e) {
            outputBoundary.presentError("An error occurred while displaying recording waveform: " + e.getMessage());
        }
    }
}

