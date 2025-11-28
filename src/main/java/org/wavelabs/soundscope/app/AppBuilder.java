package org.wavelabs.soundscope.app;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.Point;

import org.wavelabs.soundscope.data_access.FileDAO;
import org.wavelabs.soundscope.data_access.JavaSoundAudioFileGateway;
import org.wavelabs.soundscope.data_access.JavaSoundPlaybackGateway;
import org.wavelabs.soundscope.entity.Song;
import org.wavelabs.soundscope.infrastructure.ByteArrayFileSaver;
import org.wavelabs.soundscope.infrastructure.JavaMicRecorder;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintController;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintPresenter;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintViewModel;
import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.interface_adapter.play_recording.PlayRecordingController;
import org.wavelabs.soundscope.interface_adapter.play_recording.PlayRecordingPresenter;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveRecordingController;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveRecordingPresenter;
import org.wavelabs.soundscope.interface_adapter.start_recording.StartRecordingController;
import org.wavelabs.soundscope.interface_adapter.stop_recording.StopRecordingController;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.DisplayRecordingWaveformController;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.DisplayRecordingWaveformPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformViewModel;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformIB;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformOB;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintIB;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintInteractor;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveform;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintOB;
import org.wavelabs.soundscope.use_case.identify.IdentifyInteractor;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecording;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingIB;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingOB;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFile;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecording;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingIB;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingOB;
import org.wavelabs.soundscope.use_case.start_recording.StartRecording;
import org.wavelabs.soundscope.use_case.start_recording.StartRecordingIB;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecording;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecordingIB;
import org.wavelabs.soundscope.view.FingerprintView;
import org.wavelabs.soundscope.view.MainView;
import org.wavelabs.soundscope.view.UIStyle;
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
        mainView = new MainView(mainViewModel);

        mainPanel = mainView;
        return this;
    }

    // Use case view views & view models
    // NOTE: Currently, this is missing controllers, presenters, view models, etc.
    public AppBuilder addWaveFormView() {
        waveformPanel = new WaveformPanel();
        timelinePanel = new TimelinePanel();
        waveformViewModel = new WaveformViewModel();

        WaveformPresenter presenter = new WaveformPresenter(waveformViewModel);
        JavaSoundAudioFileGateway gateway = new JavaSoundAudioFileGateway();
        processAudioFileUseCase = new ProcessAudioFile(gateway, presenter);

        mainPanel.add(waveformPanel);
        mainPanel.add(mainButtonPanel); // TODO: why is this inside addWaveformView?


        // Create synchronized scroll panes for timeline and waveform
        JScrollPane timelineScrollPane = new JScrollPane(timelinePanel);
        timelineScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        timelineScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        timelineScrollPane.setBorder(null);

        waveformScrollPane = new JScrollPane(waveformPanel);
        waveformScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        waveformScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        waveformScrollPane.setBorder(null);
        // Ensure scrollbar is always visible when content is larger than viewport
        waveformScrollPane.getHorizontalScrollBar().setUnitIncrement(10);

        // Synchronize scrolling between timeline and waveform
        timelineScrollPane.getViewport().addChangeListener(e -> {
            JViewport timelineViewport = timelineScrollPane.getViewport();
            JViewport waveformViewport = waveformScrollPane.getViewport();
            waveformViewport.setViewPosition(timelineViewport.getViewPosition());
        });

        waveformScrollPane.getViewport().addChangeListener(e -> {
            JViewport timelineViewport = timelineScrollPane.getViewport();
            JViewport waveformViewport = waveformScrollPane.getViewport();
            timelineViewport.setViewPosition(waveformViewport.getViewPosition());
        });

        // Calculate width for 30 seconds: account for 256x downsampling
        int widthFor30Seconds = ((44100 * 30) / 256) / 8;
        timelineScrollPane.setPreferredSize(new Dimension(widthFor30Seconds, 30));
        waveformScrollPane.setPreferredSize(
                new Dimension(widthFor30Seconds, UIStyle.Dimensions.WAVEFORM_HEIGHT));

        // Create container for timeline and waveform
        JPanel waveformContainer = new JPanel(new BorderLayout());
        waveformContainer.add(timelineScrollPane, BorderLayout.NORTH);
        waveformContainer.add(waveformScrollPane, BorderLayout.CENTER);

        mainPanel.add(waveformContainer);
        mainPanel.add(mainButtonPanel);

        javax.swing.Timer timer = new javax.swing.Timer(100, e -> {
            if (waveformViewModel.getAudioData() != null) {
                // Get playback position from playback use case (works for both playing and paused)
                double playbackPositionSeconds = 0.0;
                if (playRecordingUseCase != null) {
                    int framesPlayed = playRecordingUseCase.getFramesPlayed();
                    int sampleRate = waveformViewModel.getAudioData().getSampleRate();
                    if (sampleRate > 0 && framesPlayed > 0) {
                        playbackPositionSeconds = (double) framesPlayed / sampleRate;
                    }
                }

                // Only update audio data if it changed, otherwise just update playback position
                // This avoids recalculating waveform paths every 100ms
                waveformPanel.updateWaveform(waveformViewModel.getAudioData(),
                        playbackPositionSeconds);

                // Update timeline with same data (only if audio data changed)
                if (timelinePanel != null) {
                    timelinePanel.updateTimeline(
                            waveformViewModel.getAudioData().getDurationSeconds(),
                            waveformViewModel.getAudioData().getSampleRate());
                }
                // Ensure scroll pane is updated after waveform changes
                waveformScrollPane.revalidate();
                waveformScrollPane.repaint();
            }
            // Add play button update logic from main
            if (playRecordingUseCase != null && playPauseButton != null) {
                String desired = playRecordingUseCase.isPlaying() ? "Pause" : "Play";
                if (!desired.equals(playPauseButton.getText())) {
                    playPauseButton.setText(desired);
                }
            }
        });
        timer.start();

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

    public AppBuilder addFingerprintView() {
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
        JButton identifyButton = new JButton("Identify");
        identifyButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(identifyButton);

        // TODO: add UI elements to display the Identify information
        // JTextArea songTitle = new JTextArea("Song: ");
        // songTitle.setPreferredSize(new Dimension(200, 200));
        // infoPanel.add(songTitle);
        //
        // JTextArea songArtist = new JTextArea("Artist: ");
        // songArtist.setPreferredSize(new Dimension(200, 200));
        // infoPanel.add(songArtist);

        final IdentifyInteractor identifyInteractor = new IdentifyInteractor(song);

        identifyButton.addActionListener(e -> {
            // TODO: handle errors and failure case
            identifyInteractor.identify((int) fileDAO.getAudioRecording().getDurationSeconds());
            System.out.println(song.getMetadata());
        });
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
