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
        PlayRecording interactorWithNullOB = new PlayRecording(mockDAI, null);

        // Test pause
        interactorWithNullOB.pause();
        Assert.assertTrue(mockDAI.pausePlaybackCalled);

        // Test stop
        interactorWithNullOB.stop();
        Assert.assertTrue(mockDAI.stopPlaybackCalled);

        // Test play (success path)
        PlayRecordingID inputData = new PlayRecordingID("path/to/audio.wav", false);
        interactorWithNullOB.play(inputData);
        Assert.assertTrue(mockDAI.startPlaybackCalled);

        // Test play (error path)
        mockDAI.shouldThrowOnLoad = true;
        PlayRecordingID inputData2 = new PlayRecordingID("path/to/audio2.wav", false);
        interactorWithNullOB.play(inputData2);
        // Should catch exception and safely ignore null OB

        // Test updateState
        interactorWithNullOB.updateState();
        // Should return safely
    }

    @Test(expected = IllegalArgumentException.class)
    public void PlayNullSourceThrowsException() {
        interactor.play(null);
    }

    @Test
    public void PlayFirstTimeLoadsAndStarts() {
        PlayRecordingID inputData = new PlayRecordingID("path/to/audio.wav", false);

        interactor.play(inputData);

        Assert.assertTrue("stopPlayback should be called before loading",
                mockDAI.stopPlaybackCalled);
        Assert.assertEquals("loadAudio should be called with correct path", "path/to/audio.wav",
                mockDAI.loadedPath);
        Assert.assertTrue("startPlayback should be called", mockDAI.startPlaybackCalled);
        Assert.assertTrue("playbackStarted should be called", mockOB.playbackStartedCalled);
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

        Assert.assertNull("loadAudio should NOT be called for same file without restart",
                mockDAI.loadedPath);
        Assert.assertTrue("startPlayback should be called", mockDAI.startPlaybackCalled);
        Assert.assertTrue("playbackStarted should be called", mockOB.playbackStartedCalled);
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
        Assert.assertEquals("loadAudio should be called for restart", "path/to/audio.wav",
                mockDAI.loadedPath);
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
        Assert.assertEquals("loadAudio should be called for new file", "path/to/audio2.wav",
                mockDAI.loadedPath);
        Assert.assertTrue("startPlayback should be called", mockDAI.startPlaybackCalled);
    }

    @Test
    public void PlayLoadFailsReportsError() {
        mockDAI.shouldThrowOnLoad = true;
        PlayRecordingID inputData = new PlayRecordingID("path/to/audio.wav", false);

        interactor.play(inputData);

        Assert.assertTrue("playbackError should be called", mockOB.playbackErrorCalled);
        Assert.assertEquals("Error message should match", "Failed to load audio file",
                mockOB.errorMessage);
    }

    @Test
    public void PausePausesAndPresents() {
        interactor.pause();
        Assert.assertTrue("pausePlayback should be called", mockDAI.pausePlaybackCalled);
        Assert.assertTrue("playbackPaused should be called", mockOB.playbackPausedCalled);
    }

    @Test
    public void StopStopsAndPresents() {
        interactor.stop();
        Assert.assertTrue("stopPlayback should be called", mockDAI.stopPlaybackCalled);
        Assert.assertTrue("playbackStopped should be called", mockOB.playbackStoppedCalled);
    }

    @Test
    public void UpdateStateGathersDataAndPresents() {
        mockDAI.isPlayingResult = true;
        mockDAI.framesPlayedResult = 100;
        mockDAI.totalFramesResult = 1000;

        interactor.updateState();

        Assert.assertTrue("updateMainState should be called", mockOB.updateMainStateCalled);
        Assert.assertNotNull("updateData should not be null", mockOB.lastUpdateData);
        Assert.assertTrue("isPlaying should be true", mockOB.lastUpdateData.isPlaying());
        Assert.assertEquals("framesPlayed should be 100", 100,
                mockOB.lastUpdateData.framesPlayed());
        Assert.assertFalse("playingFinished should be false",
                mockOB.lastUpdateData.playingFinished());
    }

    @Test
    public void UpdateStateDetectsFinished() {
        mockDAI.isPlayingResult = false;
        mockDAI.framesPlayedResult = 1000;
        mockDAI.totalFramesResult = 1000;

        interactor.updateState();

        Assert.assertTrue("updateMainState should be called", mockOB.updateMainStateCalled);
        Assert.assertTrue("playingFinished should be true",
                mockOB.lastUpdateData.playingFinished());
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
        public AudioRecording loadAudio(String sourcePath)
                throws IOException, UnsupportedAudioFileException {
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
        boolean playbackStartedCalled = false;
        boolean playbackPausedCalled = false;
        boolean playbackStoppedCalled = false;
        boolean playbackErrorCalled = false;
        boolean updateMainStateCalled = false;
        String errorMessage = null;
        PlayRecordingOD lastUpdateData = null;

        void reset() {
            playbackStartedCalled = false;
            playbackPausedCalled = false;
            playbackStoppedCalled = false;
            playbackErrorCalled = false;
            updateMainStateCalled = false;
            errorMessage = null;
            lastUpdateData = null;
        }

        @Override
        public void playbackStarted() {
            playbackStartedCalled = true;
        }

        @Override
        public void playbackPaused() {
            playbackPausedCalled = true;
        }

        @Override
        public void playbackStopped() {
            playbackStoppedCalled = true;
        }

        @Override
        public void playbackError(String message) {
            playbackErrorCalled = true;
            errorMessage = message;
        }

        @Override
        public void updateMainState(PlayRecordingOD updateData) {
            updateMainStateCalled = true;
            lastUpdateData = updateData;
        }
    }
}

