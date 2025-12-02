package org.wavelabs.soundscope.use_case.play_recording;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.UnsupportedAudioFileException;

public class PlayRecording implements PlayRecordingIB {
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
            Executors.newSingleThreadScheduledExecutor();

    private final long updateSpacingMls = 50;
    private final PlayRecordingDAI playbackGateway;
    private final PlayRecordingOB outputBoundary;
    private String loadedSourcePath;

    public PlayRecording(PlayRecordingDAI playbackGateway, PlayRecordingOB outputBoundary) {
        this.playbackGateway = Objects.requireNonNull(playbackGateway, "playbackGateway must not be null");
        this.outputBoundary = outputBoundary;
        if (this.outputBoundary != null) {
            SCHEDULED_EXECUTOR_SERVICE.schedule(this::updateState, updateSpacingMls, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void play(PlayRecordingID audioSource) {
        if (audioSource == null) {
            throw new IllegalArgumentException("audioSource must not be null");
        }

        final boolean needsReload = audioSource.shouldRestartFromBeginning()
                || loadedSourcePath == null
                || !loadedSourcePath.equals(audioSource.getSourcePath());

        if (needsReload) {
            playbackGateway.stopPlayback();
            try {
                playbackGateway.loadAudio(audioSource.getSourcePath());
                loadedSourcePath = audioSource.getSourcePath();
            }
            catch (IOException | UnsupportedAudioFileException exception) {
                if (outputBoundary != null) {
                    outputBoundary.playbackError("Failed to load audio file");
                }
            }
        }

        playbackGateway.startPlayback();
        if (outputBoundary != null) {
            outputBoundary.playbackStarted();
        }
    }

    @Override
    public void pause() {
        playbackGateway.pausePlayback();
        if (outputBoundary != null) {
            outputBoundary.playbackPaused();
        }
    }

    @Override
    public void stop() {
        playbackGateway.stopPlayback();
        if (outputBoundary != null) {
            outputBoundary.playbackStopped();
        }
    }

    /**
     * Updates the main state.
     */
    public void updateState() {
        if (outputBoundary == null) {
            return;
        }
        final PlayRecordingOD updateData = new PlayRecordingOD(
                playbackGateway.isPlaying(),
                playbackGateway.getFramesPlayed() >= playbackGateway.getTotalFrames(),
                playbackGateway.getFramesPlayed()
        );
        outputBoundary.updateMainState(updateData);
        SCHEDULED_EXECUTOR_SERVICE.schedule(this::updateState, updateSpacingMls, TimeUnit.MILLISECONDS);
    }
}
