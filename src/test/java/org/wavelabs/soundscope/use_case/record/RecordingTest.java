package org.wavelabs.soundscope.use_case.record;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wavelabs.soundscope.use_case.start_recording.RecordingOB;
import org.wavelabs.soundscope.use_case.start_recording.RecordingOD;
import org.wavelabs.soundscope.use_case.start_recording.StartRecording;
import org.wavelabs.soundscope.use_case.start_recording.StartRecordingDAI;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecording;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecordingDAI;

public class RecordingTest {
    private MockRecordingDAI mockDAI;
    private MockRecordingOB mockOB;
    private StartRecording startRecordingInteractor;
    private StopRecording stopRecordingInteractor;
    private final String UNAVAILABLE_LINE_ERROR_MESSAGE = "Unsupported Audio Line";

    @Before
    public void setUp() {
        mockDAI = new MockRecordingDAI();
        mockOB = new MockRecordingOB();
        startRecordingInteractor = new StartRecording(mockDAI, mockOB);
        stopRecordingInteractor = new StopRecording(mockDAI);
    }

    @Test
    public void testSuccessfulStartRecording() {
        // reset DAI to the initial condition
        mockDAI.reset();
        mockOB.reset();

        // execute use case
        startRecordingInteractor.execute();

        // assert
        Assert.assertTrue("DAI startRecording method must be called", mockDAI.startRecordingCalled);
        Assert.assertTrue("DAI internal state must be recording", mockDAI.isRecording());
    }

    @Test
    public void testStartRecordingDAIThrowsError() {
        mockDAI.reset();
        mockOB.reset();

        // Set up the mock DAI to simulate a failure (line unavailable)
        mockDAI.shouldThrowOnStart = true;
        mockDAI.isRecording = false;

        startRecordingInteractor.execute();

        // assert
        Assert.assertTrue("OB must present an error", mockOB.errorProvoked);
        Assert.assertTrue("Error message should explain line unavailability.", mockOB.errorMessage.contains(UNAVAILABLE_LINE_ERROR_MESSAGE));
        Assert.assertFalse("Internal state should NOT be recording", mockDAI.isRecording());
    }

    @Test
    public void testStartRecordingODUpdatesProperly() {
        mockDAI.reset();
        mockOB.reset();

        mockDAI.isRecording = true;
        startRecordingInteractor.updateRecording();
        Assert.assertTrue("Update state must be called", mockOB.updateRecordingStateCalled);
        Assert.assertTrue("Recording state must be updated", mockOB.recordingState.isPlaying());
    }

    @Test
    public void testSuccessfulStopRecording() {
        mockDAI.reset();
        // Simulate that the system is currently recording
        mockDAI.isRecording = true;
        mockDAI.startRecordingCalled = true; // Simulate previous start

        // execute use case
        stopRecordingInteractor.execute();

        // assert
        Assert.assertTrue("DAI stopRecording method must be called", mockDAI.stopRecordingCalled);
        Assert.assertFalse("DAI internal state must NOT be recording", mockDAI.isRecording());
        // Since StopRecording was constructed without an OB, we only check DAI interaction.
    }

    // Mock Classes for unit testing

    private static class MockRecordingDAI implements StartRecordingDAI, StopRecordingDAI {
        boolean startRecordingCalled = false;
        boolean stopRecordingCalled = false;
        boolean isRecording = false;
        boolean shouldThrowOnStart = false;

        void reset () {
            startRecordingCalled = false;
            stopRecordingCalled = false;
            isRecording = false;
            shouldThrowOnStart = false;
        }

        @Override
        public void startRecording() throws UnsupportedOperationException {
            if (shouldThrowOnStart) {
                throw new UnsupportedOperationException("Line not available");
            }
            startRecordingCalled = true;
            isRecording = true;
        }

        @Override
        public boolean isRecording() {
            return isRecording;
        }

        @Override
        public void stopRecording() {
            stopRecordingCalled = true;
            isRecording = false;
        }
    }

    private static class MockRecordingOB implements RecordingOB {
        boolean updateRecordingStateCalled = false;
        boolean errorProvoked = false;
        String errorMessage = null;
        RecordingOD recordingState = null;

        void reset () {
            updateRecordingStateCalled = false;
            errorProvoked = false;
            errorMessage = null;
            recordingState = null;
        }

        @Override
        public void updateRecordingState(RecordingOD newData) {
            recordingState = newData;
            updateRecordingStateCalled = true;
        }

        @Override
        public void presentError(String message) {
            errorProvoked = true;
            errorMessage = message;
        }
    }
}

