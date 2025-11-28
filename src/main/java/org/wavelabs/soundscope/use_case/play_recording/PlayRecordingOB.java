package org.wavelabs.soundscope.use_case.play_recording;

public interface PlayRecordingOB {
	void playbackStarted();

	void playbackPaused();

	void playbackStopped();

	void playbackError(String message);
}
