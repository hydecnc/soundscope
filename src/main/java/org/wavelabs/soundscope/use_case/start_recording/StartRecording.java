package org.wavelabs.soundscope.use_case.start_recording;

import org.wavelabs.soundscope.infrastructure.Recorder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StartRecording implements StartRecordingIB {
    private final static ScheduledExecutorService updateScheduler = Executors.newSingleThreadScheduledExecutor();
    private final StartRecordingDAI startRecordingDataObject;
    private final RecordingOB recordingOB;
    private final long UPDATE_SPACING_MILLIS = 100;

    public StartRecording(StartRecordingDAI startRecordingDAI, RecordingOB recordingOB) {
        this.startRecordingDataObject = startRecordingDAI;
        this.recordingOB = recordingOB;
        updateScheduler.schedule(this::updateRecording, UPDATE_SPACING_MILLIS, TimeUnit.MILLISECONDS);
    }

    /**
     * execute the actual use case
     */
    @Override
    public void execute() {
        final Recorder recorder = startRecordingDataObject.getRecorder();
        if (!recorder.isRecording()) {
            recorder.start();
        }
    }

    // Updates recording status every so often
    public void updateRecording() {
        RecordingOD outputData = new RecordingOD(
            startRecordingDataObject.getRecorder().isRecording()
        );
        recordingOB.updateRecordingState(outputData);
        updateScheduler.schedule(this::updateRecording, UPDATE_SPACING_MILLIS, TimeUnit.MILLISECONDS);
    }
}
