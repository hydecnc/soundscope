package org.wavelabs.soundscope.use_case.stop_recording;

import org.wavelabs.soundscope.entity.AudioRecording;
import org.wavelabs.soundscope.infrastructure.Recorder;

/**
 * Data Access Interface (DAI) for the stop-recording use case.
 *
 * <p>This interface provides the gateway through which the stop-recording
 * interactor accesses both the active {@link Recorder} and the in-memory
 * {@link AudioRecording} being constructed during the recording session.</p>
 *
 * <p>Implementations may wrap platform audio APIs, hold references to the
 * current recording buffer, or provide mock objects for testing. By depending
 * only on this abstraction, the use case remains decoupled from concrete audio
 * systems and storage mechanisms.</p>
 */
public interface StopRecordingDAI {
    void stopRecording();
}

