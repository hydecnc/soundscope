package org.wavelabs.soundscope.use_case.start_recording;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.wavelabs.soundscope.infrastructure.Recorder;

public class StartRecording implements StartRecordingIB {
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
            Executors.newSingleThreadScheduledExecutor();
    private final StartRecordingDAI startRecordingDataObject;
    private final RecordingOB recordingOB;
    private final long updateSpacingMls = 100;

    public StartRecording(StartRecordingDAI startRecordingDAI, RecordingOB recordingOB) {
        this.startRecordingDataObject = startRecordingDAI;
        this.recordingOB = recordingOB;
        SCHEDULED_EXECUTOR_SERVICE.schedule(this::updateRecording, updateSpacingMls, TimeUnit.MILLISECONDS);
    }

    /**
     * Execute the actual use case.
     */
    @Override
    public void execute() {
        final Recorder recorder = startRecordingDataObject.getRecorder();
        if (!recorder.isRecording()) {
            recorder.start();
        }
    }

    /**
     * Updates recording status every so often.
     */
    public void updateRecording() {
        final RecordingOD outputData = new RecordingOD(
            startRecordingDataObject.getRecorder().isRecording()
        );
        recordingOB.updateRecordingState(outputData);
        SCHEDULED_EXECUTOR_SERVICE.schedule(this::updateRecording, updateSpacingMls, TimeUnit.MILLISECONDS);
    }
}
