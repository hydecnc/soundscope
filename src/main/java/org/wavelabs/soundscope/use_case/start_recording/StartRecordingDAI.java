package org.wavelabs.soundscope.use_case.start_recording;

public interface StartRecordingDAI {
    void startRecording() throws UnsupportedOperationException;
    boolean isRecording();
}
