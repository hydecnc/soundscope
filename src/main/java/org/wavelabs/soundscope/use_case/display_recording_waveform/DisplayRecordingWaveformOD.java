package org.wavelabs.soundscope.use_case.display_recording_waveform;

import org.wavelabs.soundscope.entity.AudioData;

/**
 * Output data structure for DisplayRecordingWaveform use case.
 * Contains the output data produced by the use case.
 */
public class DisplayRecordingWaveformOD {
    private final AudioData audioData;

    /**
     * Constructs a new DisplayRecordingWaveformOD with the specified audio data.
     *
     * @param audioData The audio data containing amplitude samples for waveform display
     */
    public DisplayRecordingWaveformOD(AudioData audioData) {
        this.audioData = audioData;
    }

    /**
     * Gets the audio data for waveform display.
     *
     * @return The AudioData object containing amplitude samples and metadata
     */
    public AudioData getAudioData() {
        return audioData;
    }
}

