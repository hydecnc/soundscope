package org.wavelabs.soundscope.use_case.display_recording_waveform;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wavelabs.soundscope.entity.AudioData;

/**
 * Tests for the DisplayRecordingWaveform use case interactor.
 * 
 * <p>This test suite provides 100% code coverage for the DisplayRecordingWaveform interactor,
 * testing all code paths including:
 * <ul>
 *   <li>Constructor with valid dependencies</li>
 *   <li>execute() with null audioData (early return)</li>
 *   <li>execute() with valid audioData (success path)</li>
 *   <li>execute() with exception (error handling)</li>
 * </ul>
 */
public class DisplayRecordingWaveformTest {

    private MockDisplayRecordingWaveformDAI mockDAI;
    private MockDisplayRecordingWaveformOB mockOB;
    private DisplayRecordingWaveform interactor;

    @Before
    public void setup() {
        mockDAI = new MockDisplayRecordingWaveformDAI();
        mockOB = new MockDisplayRecordingWaveformOB();
        interactor = new DisplayRecordingWaveform(mockDAI, mockOB);
    }

    @Test
    public void constructorWithValidDependencies() {
        // Test that constructor accepts valid dependencies and creates instance
        DisplayRecordingWaveform newInteractor = new DisplayRecordingWaveform(mockDAI, mockOB);
        Assert.assertNotNull("Interactor should be created successfully", newInteractor);
    }

    @Test
    public void executeWithNullAudioDataReturnsEarly() {
        // Setup: DAI returns null (not recording)
        mockDAI.setAudioDataToReturn(null);
        mockOB.reset();

        // Execute
        DisplayRecordingWaveformID inputData = new DisplayRecordingWaveformID();
        interactor.execute(inputData);

        // Verify: Should return early without calling present or presentError
        Assert.assertFalse("present should not be called when audioData is null", 
                          mockOB.presentCalled);
        Assert.assertFalse("presentError should not be called when audioData is null", 
                          mockOB.presentErrorCalled);
        Assert.assertNull("lastOutputData should be null", mockOB.lastOutputData);
        Assert.assertNull("errorMessage should be null", mockOB.errorMessage);
    }

    @Test
    public void executeWithValidAudioDataCallsPresent() {
        // Setup: Create valid AudioData
        double[] amplitudeSamples = {0.5, -0.3, 0.8, -0.2, 0.1};
        AudioData audioData = new AudioData(
            amplitudeSamples,
            "test/path/audio.wav",
            5000L, // 5 seconds in milliseconds
            44100, // sample rate
            2 // stereo
        );
        mockDAI.setAudioDataToReturn(audioData);
        mockOB.reset();

        // Execute
        DisplayRecordingWaveformID inputData = new DisplayRecordingWaveformID();
        interactor.execute(inputData);

        // Verify: Should call present with correct output data
        Assert.assertTrue("present should be called with valid audioData", 
                         mockOB.presentCalled);
        Assert.assertFalse("presentError should not be called on success", 
                          mockOB.presentErrorCalled);
        Assert.assertNotNull("lastOutputData should not be null", mockOB.lastOutputData);
        Assert.assertEquals("AudioData in output should match input", 
                           audioData, mockOB.lastOutputData.getAudioData());
    }

    @Test
    public void executeWithEmptyAmplitudeSamples() {
        // Setup: AudioData with empty amplitude samples
        double[] emptySamples = {};
        AudioData audioData = new AudioData(
            emptySamples,
            "test/path/empty.wav",
            0L,
            44100,
            2
        );
        mockDAI.setAudioDataToReturn(audioData);
        mockOB.reset();

        // Execute
        DisplayRecordingWaveformID inputData = new DisplayRecordingWaveformID();
        interactor.execute(inputData);

        // Verify: Should still call present (empty data is valid)
        Assert.assertTrue("present should be called even with empty samples", 
                         mockOB.presentCalled);
        Assert.assertNotNull("lastOutputData should not be null", mockOB.lastOutputData);
    }

    @Test
    public void executeWithExceptionCallsPresentError() {
        // Setup: DAI throws exception
        mockDAI.setShouldThrowException(true);
        mockDAI.setExceptionToThrow(new RuntimeException("Test exception"));
        mockOB.reset();

        // Execute
        DisplayRecordingWaveformID inputData = new DisplayRecordingWaveformID();
        interactor.execute(inputData);

        // Verify: Should call presentError with exception message
        Assert.assertFalse("present should not be called when exception occurs", 
                          mockOB.presentCalled);
        Assert.assertTrue("presentError should be called when exception occurs", 
                         mockOB.presentErrorCalled);
        Assert.assertNotNull("errorMessage should not be null", mockOB.errorMessage);
        Assert.assertTrue("errorMessage should contain exception message", 
                         mockOB.errorMessage.contains("Test exception"));
        Assert.assertTrue("errorMessage should contain error prefix", 
                         mockOB.errorMessage.contains("An error occurred while displaying recording waveform"));
    }

    @Test
    public void executeWithNullPointerException() {
        // Setup: DAI throws NullPointerException
        mockDAI.setShouldThrowException(true);
        mockDAI.setExceptionToThrow(new NullPointerException("Null pointer"));
        mockOB.reset();

        // Execute
        DisplayRecordingWaveformID inputData = new DisplayRecordingWaveformID();
        interactor.execute(inputData);

        // Verify: Should handle NullPointerException
        Assert.assertTrue("presentError should be called for NullPointerException", 
                         mockOB.presentErrorCalled);
        Assert.assertTrue("errorMessage should contain NullPointerException message", 
                         mockOB.errorMessage.contains("Null pointer"));
    }

    @Test
    public void executeWithIOException() {
        // Setup: DAI throws IOException
        mockDAI.setShouldThrowException(true);
        mockDAI.setExceptionToThrow(new java.io.IOException("IO error"));
        mockOB.reset();

        // Execute
        DisplayRecordingWaveformID inputData = new DisplayRecordingWaveformID();
        interactor.execute(inputData);

        // Verify: Should handle IOException
        Assert.assertTrue("presentError should be called for IOException", 
                         mockOB.presentErrorCalled);
        Assert.assertTrue("errorMessage should contain IOException message", 
                         mockOB.errorMessage.contains("IO error"));
    }

    @Test
    public void executeWithExceptionWithNullMessage() {
        // Setup: DAI throws exception with null message
        mockDAI.setShouldThrowException(true);
        mockDAI.setExceptionToThrow(new RuntimeException());
        mockOB.reset();

        // Execute
        DisplayRecordingWaveformID inputData = new DisplayRecordingWaveformID();
        interactor.execute(inputData);

        // Verify: Should still call presentError even with null message
        Assert.assertTrue("presentError should be called even with null exception message", 
                         mockOB.presentErrorCalled);
        Assert.assertNotNull("errorMessage should not be null", mockOB.errorMessage);
    }

    @Test
    public void executeMultipleTimesWithDifferentData() {
        // Setup: First call with null
        mockDAI.setAudioDataToReturn(null);
        mockOB.reset();
        interactor.execute(new DisplayRecordingWaveformID());
        Assert.assertFalse("present should not be called when audioData is null", 
                          mockOB.presentCalled);

        // Setup: Second call with valid data
        double[] samples = {0.1, 0.2, 0.3};
        AudioData audioData = new AudioData(samples, "test.wav", 1000L, 44100, 1);
        mockDAI.setAudioDataToReturn(audioData);
        mockOB.reset();
        interactor.execute(new DisplayRecordingWaveformID());
        Assert.assertTrue("present should be called with valid data", 
                         mockOB.presentCalled);
        Assert.assertEquals("AudioData should match input", 
                           audioData, mockOB.lastOutputData.getAudioData());

        // Setup: Third call with exception
        mockDAI.setShouldThrowException(true);
        mockDAI.setExceptionToThrow(new RuntimeException("Error"));
        mockOB.reset();
        interactor.execute(new DisplayRecordingWaveformID());
        Assert.assertTrue("presentError should be called when exception occurs", 
                         mockOB.presentErrorCalled);
    }

    @Test
    public void executeWithNullInputData() {
        // Setup: Valid audioData
        double[] samples = {0.5};
        AudioData audioData = new AudioData(samples, "test.wav", 1000L, 44100, 1);
        mockDAI.setAudioDataToReturn(audioData);
        mockOB.reset();

        // Execute with null input (should still work)
        interactor.execute(null);

        // Verify: Should still process correctly
        Assert.assertTrue("present should be called even with null input", 
                         mockOB.presentCalled);
    }

    /**
     * Mock implementation of DisplayRecordingWaveformDAI for testing.
     * Allows control over what data is returned and whether exceptions are thrown.
     */
    private static class MockDisplayRecordingWaveformDAI implements DisplayRecordingWaveformDAI {
        private AudioData audioDataToReturn;
        private boolean shouldThrowException = false;
        private Exception exceptionToThrow;

        void setAudioDataToReturn(AudioData audioData) {
            this.audioDataToReturn = audioData;
            this.shouldThrowException = false;
        }

        void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        void setExceptionToThrow(Exception exception) {
            this.exceptionToThrow = exception;
            this.shouldThrowException = true;
        }

        @Override
        public AudioData getCurrentRecordingBuffer() {
            if (shouldThrowException) {
                // Wrap non-RuntimeException exceptions in RuntimeException
                // to match the catch block in DisplayRecordingWaveform.execute()
                if (exceptionToThrow instanceof RuntimeException) {
                    throw (RuntimeException) exceptionToThrow;
                } else {
                    throw new RuntimeException(exceptionToThrow);
                }
            }
            return audioDataToReturn;
        }
    }

    /**
     * Mock implementation of DisplayRecordingWaveformOB for testing.
     * Tracks method calls and stores the data passed to each method.
     */
    private static class MockDisplayRecordingWaveformOB implements DisplayRecordingWaveformOB {
        boolean presentCalled = false;
        boolean presentErrorCalled = false;
        DisplayRecordingWaveformOD lastOutputData = null;
        String errorMessage = null;

        void reset() {
            presentCalled = false;
            presentErrorCalled = false;
            lastOutputData = null;
            errorMessage = null;
        }

        @Override
        public void present(DisplayRecordingWaveformOD outputData) {
            presentCalled = true;
            lastOutputData = outputData;
        }

        @Override
        public void presentError(String errorMessage) {
            presentErrorCalled = true;
            this.errorMessage = errorMessage;
        }
    }
}
