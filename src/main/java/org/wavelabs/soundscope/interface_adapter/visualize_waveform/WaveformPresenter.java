package org.wavelabs.soundscope.interface_adapter.visualize_waveform;

import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileOB;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileOD;

/**
 * Presenter for waveform visualization use case.
 *
 * <p>Implements the ProcessAudioFileOB interface, receiving output data
 * from the use case interactor and formatting it for display in the view.
 *
 * <p>This class is part of the Interface Adapters layer and is responsible for:
 * <ul>
 *   <li>Receiving output data from the use case</li>
 *   <li>Formatting audio metadata for display</li>
 *   <li>Updating the ViewModel with processed data</li>
 *   <li>Handling and formatting error messages</li>
 * </ul>
 */
public class WaveformPresenter implements ProcessAudioFileOB {
    private final WaveformViewModel viewModel;

    /**
     * Constructs a WaveformPresenter with the specified ViewModel.
     *
     * @param viewModel The ViewModel to update with processed audio data
     */
    public WaveformPresenter(WaveformViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Presents the successful processing of an audio file.
     *
     * <p>Updates the ViewModel with the processed audio data and formats
     * metadata (duration, sample rate, channels) for display.
     *
     * @param outputData The output data containing processed audio information
     */
    @Override
    public void present(ProcessAudioFileOD outputData) {
        viewModel.setAudioData(outputData.getAudioData());

        final String metadata = formatAudioMetadata(outputData.getAudioData());
        viewModel.setOutputText(
            "File loaded: " + outputData.getFileName() + "<br>" + metadata
        );
    }

    /**
     * Presents an error that occurred during audio file processing.
     *
     * <p>Formats the error message appropriately and updates the ViewModel
     * to display the error to the user.
     *
     * @param errorMessage The error message describing what went wrong
     * @param fileName     The name of the file that caused the error
     */
    @Override
    public void presentError(String errorMessage, String fileName) {
        final String formattedError = formatError(errorMessage, fileName);
        viewModel.setOutputText(formattedError);
    }

    /**
     * Formats audio metadata for display in the view.
     *
     * @param audioData The audio data containing metadata to format
     * @return Formatted string containing duration, sample rate, and channel information
     */
    private String formatAudioMetadata(AudioData audioData) {
        if (audioData == null) {
            return "";
        }

        final String duration = String.format("%.2f", audioData.getDurationSeconds());
        return String.format(
            "Duration: %ss | Sample Rate: %d Hz | Channels: %d",
            duration,
            audioData.getSampleRate(),
            audioData.getChannels()
        );
    }

    /**
     * Formats error messages for display in the view.
     *
     * @param errorMessage The raw error message
     * @param fileName     The name of the file that caused the error
     * @return Formatted error message suitable for HTML display
     */
    private String formatError(String errorMessage, String fileName) {
        if (errorMessage.contains("Unsupported")) {
            return "Unsupported audio format.<br>"
                    + "Please use WAV format only.<br>"
                    + "File: " + fileName;
        }
        else if (errorMessage.contains("corrupted")) {
            return "File appears to be corrupted or cannot be read.<br>"
                    + "Please try a different file or re-record.";
        }
        else {
            return "Error: " + errorMessage + "<br>File: " + fileName;
        }
    }
}
