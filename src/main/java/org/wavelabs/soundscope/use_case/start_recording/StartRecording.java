package org.wavelabs.soundscope.use_case.start_recording;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Use Case Interactor for StartRecording.
 *
 * <p> Contains the application business rules for the StartRecording use case,
 * and implements the StartRecordingIB interface. </p>
 *
 * <p>This interactor initiates the audio recording by:</p>
 * <ul>
 *   <li>Using the StartRecordingDAI to get the recorder interface.</li>
 *   <li>Creating Output Data with the processed audio information</li>
 *   <li>Presenting the results through the Output Boundary</li>
 *   <li>Handling errors and presenting them through the Output Boundary</li>
 * </ul>
 */
public class StartRecording implements StartRecordingIB {
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
            Executors.newSingleThreadScheduledExecutor();
    private final StartRecordingDAI startRecordingDataObject;
    private final RecordingOB recordingPresenter;
    private final long UPDATE_SPACING_MILLIS = 100;

    public StartRecording(StartRecordingDAI startRecordingDAI, RecordingOB recordingOB) {
        this.startRecordingDataObject = startRecordingDAI;
        this.recordingPresenter = recordingOB;
        SCHEDULED_EXECUTOR_SERVICE.schedule(this::updateRecording, UPDATE_SPACING_MILLIS, TimeUnit.MILLISECONDS);
    }

    /**
     * Execute the actual use case.
     */
    @Override
    public void execute(){
        try {
            startRecordingDataObject.startRecording();
        } catch (UnsupportedOperationException e) {
            String UNSUPPORTED_AUDIO_ERROR_MESSAGE = "Unsupported Audio Line";
            recordingPresenter.presentError(UNSUPPORTED_AUDIO_ERROR_MESSAGE);
        }
    }

    // Updates recording status every so often
    public void updateRecording(){
        RecordingOD outputData = new RecordingOD(
                startRecordingDataObject.isRecording()
        );
        recordingPresenter.updateRecordingState(outputData);
        SCHEDULED_EXECUTOR_SERVICE.schedule(this::updateRecording, UPDATE_SPACING_MILLIS, TimeUnit.MILLISECONDS);
    }
}
