package org.wavelabs.soundscope.use_case.play_recording;

public record PlayRecordingOD(boolean isPlaying, boolean playingFinished, long framesPlayed) { }
