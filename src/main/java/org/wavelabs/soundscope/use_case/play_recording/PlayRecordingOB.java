package org.wavelabs.soundscope.use_case.play_recording;

public interface PlayRecordingOB {
	default void presentPlaybackStarted() {}

	default void presentPlaybackPaused() {}

	default void presentPlaybackStopped() {}

	default void presentPlaybackError(String message, Exception exception) {}
}
