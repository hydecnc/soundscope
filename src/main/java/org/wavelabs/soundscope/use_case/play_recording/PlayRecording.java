package org.wavelabs.soundscope.use_case.play_recording;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Objects;

public class PlayRecording implements PlayRecordingIB {

    private final PlayRecordingDAI playbackGateway;
    private final PlayRecordingOB outputBoundary;
    private String loadedSourcePath;

    public PlayRecording(PlayRecordingDAI playbackGateway, PlayRecordingOB outputBoundary) {
        this.playbackGateway = Objects.requireNonNull(playbackGateway, "playbackGateway must not be null");
        this.outputBoundary = outputBoundary;
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
                outputBoundary.playbackError("Failed to load audio file");
            }
        }

        playbackGateway.startPlayback();
        outputBoundary.playbackStarted();
    }

    @Override
    public void pause() {
        playbackGateway.pausePlayback();
        outputBoundary.playbackPaused();
    }

    @Override
    public void stop() {
        playbackGateway.stopPlayback();
        outputBoundary.playbackStopped();
    }

    @Override
    public boolean isPlaying() {
        return playbackGateway.isPlaying();
    }

    @Override
    public int getFramesPlayed() {
        return playbackGateway.getFramesPlayed();
    }

    @Override
    public long getTotalFrames() {
        return playbackGateway.getTotalFrames();
    }
}
