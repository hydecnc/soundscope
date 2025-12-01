package org.wavelabs.soundscope.use_case.display_recording_waveform;

import org.junit.Assert;
import org.junit.Test;
import org.wavelabs.soundscope.entity.AudioData;

/**
 * Tests for the DisplayRecordingWaveformOD output data class.
 * 
 * <p>This test suite provides 100% code coverage for DisplayRecordingWaveformOD,
 * testing the constructor and getter method.
 */
public class DisplayRecordingWaveformODTest {

    @Test
    public void constructorWithValidAudioData() {
        // Setup
        double[] amplitudeSamples = {0.5, -0.3, 0.8};
        AudioData audioData = new AudioData(
            amplitudeSamples,
            "test/path/audio.wav",
            5000L,
            44100,
            2
        );

        // Execute
        DisplayRecordingWaveformOD outputData = new DisplayRecordingWaveformOD(audioData);

        // Verify
        Assert.assertNotNull("outputData should not be null", outputData);
        Assert.assertEquals("getAudioData should return the same AudioData", 
                           audioData, outputData.getAudioData());
    }

    @Test
    public void constructorWithNullAudioData() {
        // Execute
        DisplayRecordingWaveformOD outputData = new DisplayRecordingWaveformOD(null);

        // Verify
        Assert.assertNotNull("outputData should not be null", outputData);
        Assert.assertNull("getAudioData should return null", outputData.getAudioData());
    }

    @Test
    public void constructorWithEmptyAmplitudeSamples() {
        // Setup
        double[] emptySamples = {};
        AudioData audioData = new AudioData(
            emptySamples,
            "test.wav",
            0L,
            44100,
            1
        );

        // Execute
        DisplayRecordingWaveformOD outputData = new DisplayRecordingWaveformOD(audioData);

        // Verify
        Assert.assertEquals("getAudioData should return AudioData with empty samples", 
                           audioData, outputData.getAudioData());
        Assert.assertEquals("Amplitude samples should be empty", 
                           0, outputData.getAudioData().getAmplitudeSamples().length);
    }

    @Test
    public void getAudioDataReturnsSameInstance() {
        // Setup
        AudioData audioData = new AudioData(
            new double[]{0.1, 0.2},
            "test.wav",
            1000L,
            44100,
            1
        );
        DisplayRecordingWaveformOD outputData = new DisplayRecordingWaveformOD(audioData);

        // Execute & Verify: Multiple calls should return same instance
        AudioData firstCall = outputData.getAudioData();
        AudioData secondCall = outputData.getAudioData();

        Assert.assertSame("Multiple calls to getAudioData should return same instance", 
                         firstCall, secondCall);
        Assert.assertEquals("Returned AudioData should match original", 
                           audioData, firstCall);
    }
}
