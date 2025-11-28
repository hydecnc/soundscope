package org.wavelabs.soundscope.use_case.play_recording;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wavelabs.soundscope.entity.AudioRecording;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * Tests for the PlayRecording use case interactor.
 */
public class PlayRecordingTest {

    private MockPlayRecordingDAI mockDAI;
    private MockPlayRecordingOB mockOB;
    private PlayRecording interactor;

    @Before
    public void setup() {
        mockDAI = new MockPlayRecordingDAI();
        mockOB = new MockPlayRecordingOB();
        interactor = new PlayRecording(mockDAI, mockOB);
    }

    @Test(expected = NullPointerException.class)
    public void ConstructorNullDAIThrowsException() {
        new PlayRecording(null, mockOB);
    }

    @Test
    public void ConstructorNullOBDoesNotThrowException() {
        PlayRecording interactorWithDefaultOB = new PlayRecording(mockDAI, null);
        // Should not throw exception when methods are called
        interactorWithDefaultOB.pause();
        Assert.assertTrue(mockDAI.pausePlaybackCalled);
    }

    @Test(expected = IllegalArgumentException.class)
    public void PlayNullSourceThrowsException() {
        interactor.play(null);
    }

    @Test
    public void PlayFirstTimeLoadsAndStarts() {
        PlayRecordingID inputData = new PlayRecordingID("path/to/audio.wav", false);

        interactor.play(inputData);

        Assert.assertTrue("stopPlayback should be called before loading", mockDAI.stopPlaybackCalled);
        Assert.assertEquals("loadAudio should be called with correct path", "path/to/audio.wav", mockDAI.loadedPath);
        Assert.assertTrue("startPlayback should be called", mockDAI.startPlaybackCalled);
        Assert.assertTrue("presentPlaybackStarted should be called", mockOB.presentPlaybackStartedCalled);
    }

    @Test
    public void PlaySameFileStartsOnly() {
        PlayRecordingID inputData1 = new PlayRecordingID("path/to/audio.wav", false);
        interactor.play(inputData1);

        // reset mock state
        mockDAI.reset();
        mockOB.reset();

        PlayRecordingID inputData2 = new PlayRecordingID("path/to/audio.wav", false);
        interactor.play(inputData2);

        Assert.assertNull("loadAudio should NOT be called for same file without restart", mockDAI.loadedPath);
        Assert.assertTrue("startPlayback should be called", mockDAI.startPlaybackCalled);
        Assert.assertTrue("presentPlaybackStarted should be called", mockOB.presentPlaybackStartedCalled);
    }

    @Test
    public void PlaySameFileRestartReloadsAndStarts() {
        PlayRecordingID inputData1 = new PlayRecordingID("path/to/audio.wav", false);
        interactor.play(inputData1);

        // reset mock state
        mockDAI.reset();
        mockOB.reset();

        PlayRecordingID inputData2 = new PlayRecordingID("path/to/audio.wav", true);
        interactor.play(inputData2);

        Assert.assertTrue("stopPlayback should be called", mockDAI.stopPlaybackCalled);
        Assert.assertEquals("loadAudio should be called for restart", "path/to/audio.wav", mockDAI.loadedPath);
        Assert.assertTrue("startPlayback should be called", mockDAI.startPlaybackCalled);
    }

    @Test
    public void PlayDifferentFileReloadsAndStarts() {
        PlayRecordingID inputData1 = new PlayRecordingID("path/to/audio1.wav", false);
        interactor.play(inputData1);

        // Reset mock state
        mockDAI.reset();
        mockOB.reset();

        PlayRecordingID inputData2 = new PlayRecordingID("path/to/audio2.wav", false);
        interactor.play(inputData2);

        Assert.assertTrue("stopPlayback should be called", mockDAI.stopPlaybackCalled);
        Assert.assertEquals("loadAudio should be called for new file", "path/to/audio2.wav", mockDAI.loadedPath);
        Assert.assertTrue("startPlayback should be called", mockDAI.startPlaybackCalled);
    }

    @Test(expected = IllegalStateException.class)
    public void PlayLoadFailsReportsErrorAndThrowsException() {
        mockDAI.shouldThrowOnLoad = true;
        PlayRecordingID inputData = new PlayRecordingID("path/to/audio.wav", false);

        try {
            interactor.play(inputData);
        } catch (IllegalStateException e) {
            Assert.assertTrue("presentPlaybackError should be called", mockOB.presentPlaybackErrorCalled);
            Assert.assertEquals("Error message should match", "Failed to load audio file", mockOB.errorMessage);
            throw e;
        }
    }

    @Test
    public void PausePausesAndPresents() {
        interactor.pause();
        Assert.assertTrue("pausePlayback should be called", mockDAI.pausePlaybackCalled);
        Assert.assertTrue("presentPlaybackPaused should be called", mockOB.presentPlaybackPausedCalled);
    }

    @Test
    public void StopStopsAndPresents() {
        interactor.stop();
        Assert.assertTrue("stopPlayback should be called", mockDAI.stopPlaybackCalled);
        Assert.assertTrue("presentPlaybackStopped should be called", mockOB.presentPlaybackStoppedCalled);
    }

    @Test
    public void IsPlayingDelegatesToDAI() {
        mockDAI.isPlayingResult = true;
        Assert.assertTrue(interactor.isPlaying());

        mockDAI.isPlayingResult = false;
        Assert.assertFalse(interactor.isPlaying());
    }

    @Test
    public void GetFramesPlayedDelegatesToDAI() {
        mockDAI.framesPlayedResult = 123;
        Assert.assertEquals(123, interactor.getFramesPlayed());
    }

    @Test
    public void TotalFramesDelegatesToDAI() {
        mockDAI.totalFramesResult = 456L; // long
        Assert.assertEquals(456L, interactor.getTotalFrames());
    }

    // Mock classes

    private static class MockPlayRecordingDAI implements PlayRecordingDAI {
        boolean stopPlaybackCalled = false;
        boolean startPlaybackCalled = false;
        boolean pausePlaybackCalled = false;
        String loadedPath = null;
        boolean shouldThrowOnLoad = false;
        boolean isPlayingResult = false;
        int framesPlayedResult = 0;
        long totalFramesResult = 0;

        void reset() {
            stopPlaybackCalled = false;
            startPlaybackCalled = false;
            pausePlaybackCalled = false;
            loadedPath = null;
            shouldThrowOnLoad = false;
        }

        @Override
        public AudioRecording loadAudio(String sourcePath) throws IOException, UnsupportedAudioFileException {
            if (shouldThrowOnLoad) {
                throw new IOException("Mock IO Exception");
            }
            this.loadedPath = sourcePath;
            return null; // no audio recording to return
        }

        @Override
        public void startPlayback() {
            startPlaybackCalled = true;
        }

        @Override
        public void pausePlayback() {
            pausePlaybackCalled = true;
        }

        @Override
        public void stopPlayback() {
            stopPlaybackCalled = true;
        }

        @Override
        public boolean isPlaying() {
            return isPlayingResult;
        }

        @Override
        public int getFramesPlayed() {
            return framesPlayedResult;
        }

        @Override
        public long getTotalFrames() {
            return totalFramesResult;
        }
    }

    private static class MockPlayRecordingOB implements PlayRecordingOB {
        boolean presentPlaybackStartedCalled = false;
        boolean presentPlaybackPausedCalled = false;
        boolean presentPlaybackStoppedCalled = false;
        boolean presentPlaybackErrorCalled = false;
        String errorMessage = null;

        void reset() {
            presentPlaybackStartedCalled = false;
            presentPlaybackPausedCalled = false;
            presentPlaybackStoppedCalled = false;
            presentPlaybackErrorCalled = false;
            errorMessage = null;
        }

        @Override
        public void presentPlaybackStarted() {
            presentPlaybackStartedCalled = true;
        }

        @Override
        public void presentPlaybackPaused() {
            presentPlaybackPausedCalled = true;
        }

        @Override
        public void presentPlaybackStopped() {
            presentPlaybackStoppedCalled = true;
        }

        @Override
        public void presentPlaybackError(String message, Exception exception) {
            presentPlaybackErrorCalled = true;
            errorMessage = message;
        }
    }
}
