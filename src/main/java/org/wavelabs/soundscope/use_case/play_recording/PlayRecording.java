package org.wavelabs.soundscope.use_case.play_recording;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayRecording implements PlayRecordingIB {
    private final long UPDATE_SPACING_MILLIS = 50;
    private final static ScheduledExecutorService updateScheduler = Executors.newSingleThreadScheduledExecutor();
    private final PlayRecordingDAI playbackGateway;
    private final PlayRecordingOB outputBoundary;
    private String loadedSourcePath;

    public PlayRecording(PlayRecordingDAI playbackGateway, PlayRecordingOB outputBoundary) {
        this.playbackGateway = Objects.requireNonNull(playbackGateway, "playbackGateway must not be null");
        this.outputBoundary = outputBoundary;
        if (this.outputBoundary != null) {
            updateScheduler.schedule(this::updateState, UPDATE_SPACING_MILLIS, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void play(PlayRecordingID audioSource) {
        if (audioSource == null) {
            throw new IllegalArgumentException("audioSource must not be null");
        }

        boolean needsReload = audioSource.shouldRestartFromBeginning()
                || loadedSourcePath == null
                || !loadedSourcePath.equals(audioSource.getSourcePath());

        if (needsReload) {
            playbackGateway.stopPlayback();
            try {
                playbackGateway.loadAudio(audioSource.getSourcePath());
                loadedSourcePath = audioSource.getSourcePath();
            } catch (IOException | UnsupportedAudioFileException e) {
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

    //Updates the main state
    public void updateState(){
        if (outputBoundary == null) {
            return;
        }
        PlayRecordingOD updateData = new PlayRecordingOD(
                playbackGateway.isPlaying(),
                playbackGateway.getFramesPlayed() >= playbackGateway.getTotalFrames(),
                playbackGateway.getFramesPlayed()
        );
        outputBoundary.updateMainState(updateData);
        updateScheduler.schedule(this::updateState, UPDATE_SPACING_MILLIS, TimeUnit.MILLISECONDS);
    }
}
