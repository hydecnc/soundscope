package org.wavelabs.soundscope.app;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.wavelabs.soundscope.data_access.AcousticIDIdentify;
import org.wavelabs.soundscope.data_access.FileDAO;
import org.wavelabs.soundscope.data_access.JavaSoundAudioFileGateway;
import org.wavelabs.soundscope.data_access.JavaSoundPlaybackGateway;
import org.wavelabs.soundscope.entity.Song;
import org.wavelabs.soundscope.infrastructure.ByteArrayFileSaver;
import org.wavelabs.soundscope.infrastructure.JavaMicRecorder;
import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintController;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintPresenter;
import org.wavelabs.soundscope.interface_adapter.identify.IdentifyController;
import org.wavelabs.soundscope.interface_adapter.identify.IdentifyPresenter;
import org.wavelabs.soundscope.interface_adapter.play_recording.PlayRecordingController;
import org.wavelabs.soundscope.interface_adapter.play_recording.PlayRecordingPresenter;
import org.wavelabs.soundscope.interface_adapter.process_audio_file.ProcessAudioFileController;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveRecordingController;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveRecordingPresenter;
import org.wavelabs.soundscope.interface_adapter.start_recording.RecordingPresenter;
import org.wavelabs.soundscope.interface_adapter.start_recording.StartRecordingController;
import org.wavelabs.soundscope.interface_adapter.stop_recording.StopRecordingController;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.DisplayRecordingWaveformController;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.DisplayRecordingWaveformPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformViewModel;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveform;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformIB;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformOB;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintIB;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintInteractor;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintOB;
import org.wavelabs.soundscope.use_case.identify.IdentifyDAI;
import org.wavelabs.soundscope.use_case.identify.IdentifyIB;
import org.wavelabs.soundscope.use_case.identify.IdentifyInteractor;
import org.wavelabs.soundscope.use_case.identify.IdentifyOB;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecording;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingIB;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingOB;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFile;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileIB;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileOB;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecording;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingIB;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingOB;
import org.wavelabs.soundscope.use_case.start_recording.RecordingOB;
import org.wavelabs.soundscope.use_case.start_recording.StartRecording;
import org.wavelabs.soundscope.use_case.start_recording.StartRecordingIB;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecording;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecordingIB;
import org.wavelabs.soundscope.view.MainView;

/**
 * Builder class for constructing the Soundscope application.
 * Uses the Builder pattern to assemble all use cases, controllers, and views.
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity",
    "checkstyle:SuppressWarnings"})
public class AppBuilder {
    private MainView mainView;
    private MainViewModel mainViewModel;
    // TODO: remove this in some refactor eventually
    private WaveformViewModel waveformViewModel;

    private JPanel mainPanel;
    private FileDAO fileDAO = new FileDAO();
    private Song song = new Song();

    /**
     * Constructs a new AppBuilder instance.
     */
    public AppBuilder() {
    }

    /**
     * This function adds the main view, which encapsulates all views to the app.
     *
     * @return returns the AppBuilder
     */
    public AppBuilder addMainView() {
        mainViewModel = new MainViewModel();
        waveformViewModel = new WaveformViewModel();

        mainView = new MainView(mainViewModel, waveformViewModel);
        mainPanel = mainView;
        return this;
    }

    /**
     * Adds the file save use case to the application.
     * Configures the save recording functionality with ByteArrayFileSaver.
     *
     * @return the AppBuilder instance for method chaining
     */
    public AppBuilder addFileSaveUseCase() {
        fileDAO.setFileSaver(new ByteArrayFileSaver());

        final SaveRecordingOB saveRecordingOutput = new SaveRecordingPresenter(mainViewModel);
        final SaveRecordingIB saveRecordingInteractor = new SaveRecording(fileDAO, saveRecordingOutput);

        final SaveRecordingController saveRecordingController = new SaveRecordingController(saveRecordingInteractor);
        mainView.setSaveRecordingController(saveRecordingController);
        return this;
    }

    /**
     * Adds the play recording use case to the application.
     * Configures audio playback functionality using JavaSound.
     *
     * @return the AppBuilder instance for method chaining
     */
    public AppBuilder addPlayUseCase() {
        final PlayRecordingOB playRecordingOutput = new PlayRecordingPresenter(mainViewModel);
        final PlayRecordingIB playRecordingInteractor =
            new PlayRecording(new JavaSoundPlaybackGateway(), playRecordingOutput);

        final PlayRecordingController playRecordingController = new PlayRecordingController(playRecordingInteractor);
        mainView.setPlayRecordingController(playRecordingController);
        return this;
    }

    /**
     * Adds the start recording use case to the application.
     * Configures microphone recording functionality using JavaMicRecorder.
     *
     * @return the AppBuilder instance for method chaining
     */
    public AppBuilder addStartRecordUseCase() {
        fileDAO.setRecorder(new JavaMicRecorder());

        final RecordingOB recordingOutput = new RecordingPresenter(mainViewModel);

        final StartRecordingIB startRecordingInteractor = new StartRecording(fileDAO, recordingOutput);
        final StartRecordingController startRecordingController =
            new StartRecordingController(startRecordingInteractor);

        mainView.setStartRecordingController(startRecordingController);
        return this;
    }

    /**
     * Adds the stop recording use case to the application.
     * Configures functionality to stop ongoing audio recordings.
     *
     * @return the AppBuilder instance for method chaining
     */
    public AppBuilder addStopRecordUseCase() {
        final StopRecordingIB stopRecordingInteractor = new StopRecording(fileDAO);

        final StopRecordingController stopRecordingController = new StopRecordingController(stopRecordingInteractor);
        mainView.setStopRecordingController(stopRecordingController);
        return this;
    }

    /**
     * Adds the display recording waveform use case to the application.
     * Configures waveform visualization for recorded audio.
     *
     * @return the AppBuilder instance for method chaining
     */
    public AppBuilder addDisplayRecordingWaveformUseCase() {
        final DisplayRecordingWaveformOB recordingPresenter = new DisplayRecordingWaveformPresenter(waveformViewModel);
        final DisplayRecordingWaveformIB displayRecordingWaveformInteractor =
            new DisplayRecordingWaveform(fileDAO, recordingPresenter);

        final DisplayRecordingWaveformController displayRecordingWaveformController =
            new DisplayRecordingWaveformController(displayRecordingWaveformInteractor);
        mainView.setDisplayRecordingWaveformController(displayRecordingWaveformController);

        return this;
    }

    /**
     * Adds the audio fingerprinting use case to the application.
     * Configures functionality to generate acoustic fingerprints from audio.
     *
     * @return the AppBuilder instance for method chaining
     */
    public AppBuilder addFingerprintUseCase() {
        final FingerprintOB fingerprintOutputBoundary = new FingerprintPresenter(mainViewModel);
        final FingerprintIB
            fingerprintInteractor = new FingerprintInteractor(fileDAO, song, fingerprintOutputBoundary);
        final FingerprintController fingerprintController = new FingerprintController(fingerprintInteractor);
        mainView.setFingerprintController(fingerprintController);
        return this;
    }

    /**
     * Adds the process audio file use case to the application.
     * Configures functionality to load and process audio files.
     *
     * @return the AppBuilder instance for method chaining
     */
    public AppBuilder addProcessAudioFileUseCase() {
        final ProcessAudioFileOB processAudioFileOB = new WaveformPresenter(waveformViewModel);
        // TODO: should this be renamed to a ProcessAudioFilePresenter?
        final ProcessAudioFileIB processAudioFileInteractor =
            new ProcessAudioFile(new JavaSoundAudioFileGateway(), processAudioFileOB);

        final ProcessAudioFileController processAudioFileController =
            new ProcessAudioFileController(processAudioFileInteractor, fileDAO);
        mainView.setProcessAudioFileController(processAudioFileController);
        return this;
    }

    /**
     * Adds the song identification use case to the application.
     * Configures functionality to identify songs using acoustic fingerprints.
     *
     * @return the AppBuilder instance for method chaining
     */
    public AppBuilder addIdentifyUseCase() {
        final IdentifyDAI identifier = AcousticIDIdentify.getAcousicIDIdentify();
        final IdentifyOB identifyOutputBoundary = new IdentifyPresenter(mainViewModel);
        final IdentifyIB identifyInteractor = new IdentifyInteractor(song, identifyOutputBoundary, identifier);

        final IdentifyController identifyController = new IdentifyController(identifyInteractor);
        mainView.setIdentifyController(identifyController);
        return this;
    }

    /**
     * Builds and returns the complete application JFrame.
     * Sets up the frame with appropriate size, close operation, and content pane.
     *
     * @return the configured JFrame containing the Soundscope application
     */
    public JFrame build() {
        final JFrame application = new JFrame("Soundscope");
        final int applicationWidth = 600;
        final int applicationHeight = 600;
        application.setMinimumSize(new Dimension(applicationWidth, applicationHeight));
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.setContentPane(mainPanel);
        // NOTE: Consider adding view manager model
        return application;
    }
}

