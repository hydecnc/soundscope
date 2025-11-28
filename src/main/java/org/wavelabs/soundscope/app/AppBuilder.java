package org.wavelabs.soundscope.app;

import java.awt.Dimension;
import javax.swing.*;

import org.wavelabs.soundscope.data_access.FileDAO;
import org.wavelabs.soundscope.data_access.JavaSoundPlaybackGateway;
import org.wavelabs.soundscope.entity.Song;
import org.wavelabs.soundscope.infrastructure.ByteArrayFileSaver;
import org.wavelabs.soundscope.infrastructure.JavaMicRecorder;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintController;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintPresenter;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintViewModel;
import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.interface_adapter.identify.IdentifyController;
import org.wavelabs.soundscope.interface_adapter.identify.IdentifyPresenter;
import org.wavelabs.soundscope.interface_adapter.play_recording.PlayRecordingController;
import org.wavelabs.soundscope.interface_adapter.play_recording.PlayRecordingPresenter;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveRecordingController;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveRecordingPresenter;
import org.wavelabs.soundscope.interface_adapter.start_recording.StartRecordingController;
import org.wavelabs.soundscope.interface_adapter.stop_recording.StopRecordingController;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.DisplayRecordingWaveformController;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.DisplayRecordingWaveformPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformViewModel;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformIB;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformOB;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintIB;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintInteractor;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveform;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintOB;
import org.wavelabs.soundscope.use_case.identify.IdentifyIB;
import org.wavelabs.soundscope.use_case.identify.IdentifyInteractor;
import org.wavelabs.soundscope.use_case.identify.IdentifyOB;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecording;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingIB;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingOB;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecording;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingIB;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingOB;
import org.wavelabs.soundscope.use_case.start_recording.StartRecording;
import org.wavelabs.soundscope.use_case.start_recording.StartRecordingIB;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecording;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecordingIB;
import org.wavelabs.soundscope.view.FingerprintView;
import org.wavelabs.soundscope.view.MainView;
import org.wavelabs.soundscope.view.components.WaveformPanel;

import org.wavelabs.soundscope.view.components.TimelinePanel;

public class AppBuilder {
    private MainView mainView;
    private MainViewModel mainViewModel;

    private JPanel mainPanel;
    private FileDAO fileDAO = new FileDAO();

    private final JPanel mainButtonPanel = new JPanel();
    private final JPanel titlePanel = new JPanel();
    private WaveformPanel waveformPanel;
    private TimelinePanel timelinePanel;
    private JScrollPane waveformScrollPane;
    private WaveformViewModel waveformViewModel;

    private Song song = new Song(); // TODO: refactor this to use clean architecture; entities
                                    // probably shouldn't be directly referenced here?

    private FingerprintViewModel fingerprintViewModel;
    private FingerprintView fingerprintView;


    public AppBuilder() {
        mainButtonPanel.setLayout(new BoxLayout(mainButtonPanel, BoxLayout.X_AXIS));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(titlePanel);
    }

    public AppBuilder addMainView(){
        mainViewModel = new MainViewModel();
        waveformViewModel = new WaveformViewModel();

        mainView = new MainView(mainViewModel, waveformViewModel);
        mainPanel = mainView;
        return this;
    }

    public AppBuilder addFileSaveUseCase() {
        fileDAO.setFileSaver(new ByteArrayFileSaver());

        final SaveRecordingOB saveRecordingOutput = new SaveRecordingPresenter(mainViewModel);
        final SaveRecordingIB saveRecordingInteractor = new SaveRecording(fileDAO, saveRecordingOutput);

        final SaveRecordingController saveRecordingController = new SaveRecordingController(saveRecordingInteractor);
        mainView.setSaveRecordingController(saveRecordingController);
        return this;
    }

    public AppBuilder addPlayUseCase() {
        final PlayRecordingOB playRecordingOutput = new PlayRecordingPresenter(mainViewModel);
        final PlayRecordingIB playRecordingInteractor = new PlayRecording(new JavaSoundPlaybackGateway(), playRecordingOutput);

        final PlayRecordingController playRecordingController = new PlayRecordingController(playRecordingInteractor);
        mainView.setPlayRecordingController(playRecordingController);
        return this;
    }

    public AppBuilder addStartRecordUseCase() {
        fileDAO.setRecorder(new JavaMicRecorder());

        final StartRecordingIB startRecordingInteractor = new StartRecording(fileDAO);
        final StartRecordingController startRecordingController = new StartRecordingController(startRecordingInteractor);

        mainView.setStartRecordingController(startRecordingController);
        return this;
    }

    public AppBuilder addStopRecordUseCase(){
        final StopRecordingIB stopRecordingInteractor = new StopRecording(fileDAO);

        final StopRecordingController stopRecordingController = new StopRecordingController(stopRecordingInteractor);
        mainView.setStopRecordingController(stopRecordingController);
        return this;
    }

    public AppBuilder addDisplayRecordingWaveformUseCase(){
        final DisplayRecordingWaveformOB recordingPresenter = new DisplayRecordingWaveformPresenter(waveformViewModel);
        final DisplayRecordingWaveformIB displayRecordingWaveformInteractor = new DisplayRecordingWaveform(fileDAO, recordingPresenter);

        final DisplayRecordingWaveformController displayRecordingWaveformController = new DisplayRecordingWaveformController(displayRecordingWaveformInteractor);
        mainView.setDisplayRecordingWaveformController(displayRecordingWaveformController);

        return this;
    }

    public AppBuilder addFingerprintView() { //TODO: move this into the right view model
        fingerprintViewModel = new FingerprintViewModel();
        fingerprintView = new FingerprintView(fingerprintViewModel);
        mainButtonPanel.add(fingerprintView);
        return this;
    }

    public AppBuilder addFingerprintUseCase() {
        final FingerprintOB fingerprintOutputBoundary = new FingerprintPresenter(fingerprintViewModel);
        final FingerprintIB
            fingerprintInteractor = new FingerprintInteractor(fileDAO, fingerprintOutputBoundary);
        final FingerprintController fingerprintController = new FingerprintController(fingerprintInteractor);
        fingerprintView.setFingerprintController(fingerprintController);
        return this;
    }

    public AppBuilder addIdentifyUseCase() {
        final IdentifyOB identifyOutputBoundary = new IdentifyPresenter(mainViewModel);
        final IdentifyIB identifyInteractor = new IdentifyInteractor(song, identifyOutputBoundary);

        final IdentifyController identifyController = new IdentifyController(identifyInteractor);
        mainView.setIdentifyController(identifyController);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Soundscope");
        application.setMinimumSize(new Dimension(600, 600));
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        application.setContentPane(mainPanel);
        // NOTE: Consider adding view manager model
        return application;
    }
}
