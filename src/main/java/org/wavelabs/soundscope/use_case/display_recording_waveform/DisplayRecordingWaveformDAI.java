package org.wavelabs.soundscope.use_case.display_recording_waveform;

import org.wavelabs.soundscope.entity.AudioData;

/**
 * Data Access Interface for DisplayRecordingWaveform use case.
 * Defines the contract for accessing real-time recording data.
 * Implemented by the Data Access layer.
 */
public interface DisplayRecordingWaveformDAI {
    /**
     * Gets the current audio buffer from the recording and processes it to extract amplitude samples.
     *
     * @return AudioData containing amplitude samples and metadata from the current recording buffer,
     *      or null if not currently recording
     */
    AudioData getCurrentRecordingBuffer();
}

