package org.wavelabs.soundscope.interface_adapter.visualize_waveform;

import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformIB;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformID;

public class DisplayRecordingWaveformController {
    private final DisplayRecordingWaveformIB displayWaveformInteractor;

    public DisplayRecordingWaveformController(DisplayRecordingWaveformIB displayWaveformInteractor) {
        this.displayWaveformInteractor = displayWaveformInteractor;
    }

    /**
     * Executes the waveform interactor.
     */
    public void execute() {
        displayWaveformInteractor.execute(new DisplayRecordingWaveformID());
    }
}
