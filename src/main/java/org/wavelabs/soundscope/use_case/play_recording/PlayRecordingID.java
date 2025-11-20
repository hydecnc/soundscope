package org.wavelabs.soundscope.use_case.play_recording;

public class PlayRecordingID {
	private final String sourcePath;
	private final boolean restartFromBeginning;

	public PlayRecordingID(String sourcePath, boolean restartFromBeginning) {
		if (sourcePath == null || sourcePath.isBlank()) {
			throw new IllegalArgumentException("sourcePath must not be blank");
		}
		this.sourcePath = sourcePath;
		this.restartFromBeginning = restartFromBeginning;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public boolean shouldRestartFromBeginning() {
		return restartFromBeginning;
	}
}
