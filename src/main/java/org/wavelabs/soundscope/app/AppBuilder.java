package org.wavelabs.soundscope.app;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import java.awt.Point;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.wavelabs.soundscope.data_access.FileDAO;
import org.wavelabs.soundscope.data_access.JavaSoundLoaderGateway;
import org.wavelabs.soundscope.entity.Song;
import org.wavelabs.soundscope.infrastructure.ByteArrayFileSaver;
import org.wavelabs.soundscope.infrastructure.JavaMicRecorder;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveFilePresenter;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveFileState;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.DisplayRecordingWaveformPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformViewModel;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprinterIB;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprinterInteractor;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveform;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformID;
import org.wavelabs.soundscope.use_case.identify.IdentifyInteractor;
import org.wavelabs.soundscope.use_case.load_audio.LoadAudio;
import org.wavelabs.soundscope.use_case.load_audio.LoadAudioID;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecording;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingIB;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingID;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingOB;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFile;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileID;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecording;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingID;
import org.wavelabs.soundscope.use_case.start_recording.StartRecording;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecording;
import org.wavelabs.soundscope.view.UIStyle;
import org.wavelabs.soundscope.view.components.WaveformPanel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.wavelabs.soundscope.view.components.TimelinePanel;

public class AppBuilder {
    private final JPanel mainPanel = new JPanel();
    private final JPanel mainButtonPanel = new JPanel();
    private final JPanel titlePanel = new JPanel();
    private WaveformPanel waveformPanel;
    private TimelinePanel timelinePanel;
    private JScrollPane waveformScrollPane;
    private WaveformViewModel waveformViewModel;
    private ProcessAudioFile processAudioFileUseCase;
    private LoadAudio loadAudioUseCase;
    private final FileDAO fileDAO;
    private JavaSoundLoaderGateway gateway = new JavaSoundLoaderGateway();
    private static boolean playing = false; // TODO: decide if it's worth moving this into the play use case

    private DisplayRecordingWaveform displayRecordingWaveformUseCase;
    private javax.swing.Timer recordingWaveformTimer;
    private PlayRecordingIB playRecordingUseCase;
    private JButton playPauseButton;
    private String currentAudioSourcePath;
    private Song song = new Song(); //TODO: refactor this to use clean architecture; entities probably shouldn't be directly referenced here?


    public AppBuilder() {
        mainButtonPanel.setLayout(new BoxLayout(mainButtonPanel, BoxLayout.X_AXIS));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(titlePanel);

        fileDAO = new FileDAO();
    }

    public AppBuilder addTitle() {
        JLabel titleLabel = new JLabel("Soundscope");
        titleLabel.setFont(new Font("Sans Serif", Font.BOLD, 36));
        titlePanel.add(titleLabel);
        return this;
    }

    // Use case view views & view models
    // NOTE: Currently, this is missing controllers, presenters, view models, etc.
    public AppBuilder addWaveFormView() {
        waveformPanel = new WaveformPanel();
        timelinePanel = new TimelinePanel();
        waveformViewModel = new WaveformViewModel();

        WaveformPresenter presenter = new WaveformPresenter(waveformViewModel);
        processAudioFileUseCase = new ProcessAudioFile(this.gateway, presenter);
        loadAudioUseCase = new LoadAudio(gateway, presenter);

        mainPanel.add(waveformPanel);
        mainPanel.add(mainButtonPanel); //TODO: why is this inside addWaveformView?


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
        timelineScrollPane.setPreferredSize(new Dimension(
            widthFor30Seconds,
            30
        ));
        waveformScrollPane.setPreferredSize(new Dimension(
            widthFor30Seconds,
            UIStyle.Dimensions.WAVEFORM_HEIGHT
        ));

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
                waveformPanel.updateWaveform(waveformViewModel.getAudioData(), playbackPositionSeconds);

                // Update timeline with same data (only if audio data changed)
                if (timelinePanel != null) {
                    timelinePanel.updateTimeline(
                        waveformViewModel.getAudioData().getDurationSeconds(),
                        waveformViewModel.getAudioData().getSampleRate()
                    );
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

    public AppBuilder addOpenFileUseCase() {
        JButton openButton = new JButton("Open");
        openButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(openButton);

        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setDialogTitle("Select Audio File");

            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "WAV Audio Files", "wav");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);

            int result = fileChooser.showOpenDialog(mainPanel);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (processAudioFileUseCase != null) {

                    LoadAudioID inputData = new LoadAudioID(selectedFile);
                    loadAudioUseCase.execute(inputData);
                }
                currentAudioSourcePath = selectedFile.getAbsolutePath();
                if (playRecordingUseCase != null) {
                    playRecordingUseCase.stop();
                }
                if (playPauseButton != null) {
                    playPauseButton.setText("Play");
                }
            }
        });

        return this;
    }

    public AppBuilder addFileSaveUseCase() {
        JButton saveAsButton = new JButton("Save As");
        saveAsButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(saveAsButton);
        saveAsButton.addActionListener(e -> {
            saveFileToDirectory();
        });
        return this;
    }

    private void saveFileToDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle("Save Audio File");

        // Filters to only WAV files. For simplicity
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "WAV Audio files (*.wav)", "wav"
        ));

        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        String formattedDate = myDateObj.format(myFormatObj);

        chooser.setSelectedFile(new File(formattedDate + ".wav"));

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File outputFile = chooser.getSelectedFile();

            // Optional: ensure extension .txt exists
            if (!outputFile.getName().contains(".")) {
                outputFile = new File(outputFile.getAbsolutePath() + ".wav");
            }

            SaveFileState state = new SaveFileState();
            SaveFilePresenter presenter = new SaveFilePresenter(state);
            SaveRecording saveRecording = new SaveRecording(fileDAO, presenter);

            saveRecording.execute(new SaveRecordingID(outputFile.getAbsolutePath()));

            if(state.isSuccess()) {
                System.out.println("Recording saved");
            } else {
                JOptionPane.showMessageDialog(mainPanel, state.getErrorMessage(),
                        "Error during save", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            System.out.println("No Selection");
            // TODO: create an error code or something; alternate flow
            return;
        };
    }

    public AppBuilder addPlayUseCase() {
        if (playRecordingUseCase == null) {
            playRecordingUseCase = new PlayRecording(new JavaSoundLoaderGateway(), new PlayRecordingOB() {});
        }

        playPauseButton = new JButton("Play");
        playPauseButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(playPauseButton);
        playPauseButton.addActionListener(e -> {
            if (currentAudioSourcePath == null || currentAudioSourcePath.isBlank()) {
                JOptionPane.showMessageDialog(mainPanel, "Please open or record audio before playing.",
                        "No audio selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (playRecordingUseCase.isPlaying()) {
                    playRecordingUseCase.pause();
                    playPauseButton.setText("Play");
                } else {
                    if (playRecordingUseCase.getTotalFrames() == playRecordingUseCase.getFramesPlayed()) {
                        playRecordingUseCase.play(new PlayRecordingID(currentAudioSourcePath, true));
                    } else {
                        playRecordingUseCase.play(new PlayRecordingID(currentAudioSourcePath, false));
                        playPauseButton.setText("Pause");
                    }
                }
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(mainPanel, ex.getMessage(), "Playback error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return this;
    }

    public AppBuilder addRecordUseCase() {
        JButton recordButton = new JButton("Start Recording");
        recordButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(recordButton);

        fileDAO.setFileSaver(new ByteArrayFileSaver());
        fileDAO.setRecorder(new JavaMicRecorder());

        SaveFilePresenter saveFilePresenter = new SaveFilePresenter(new SaveFileState());
        StartRecording startRecording = new StartRecording(fileDAO);
        StopRecording stopRecording = new StopRecording(fileDAO);
        SaveRecording saveRecording = new SaveRecording(fileDAO, saveFilePresenter);

        // Set up DisplayRecordingWaveform use case
        DisplayRecordingWaveformPresenter recordingPresenter =
            new DisplayRecordingWaveformPresenter(waveformViewModel);
        displayRecordingWaveformUseCase = new DisplayRecordingWaveform(fileDAO, recordingPresenter);

        // Timer to update waveform during recording
        recordingWaveformTimer = new Timer(50, e -> {
            if (fileDAO.getRecorder() != null && fileDAO.getRecorder().isRecording()) {
                DisplayRecordingWaveformID inputData = new DisplayRecordingWaveformID();
                displayRecordingWaveformUseCase.execute(inputData);
                // Force immediate update and auto-scroll to show latest
                if (waveformViewModel.getAudioData() != null && waveformPanel != null) {
                    waveformPanel.updateWaveform(waveformViewModel.getAudioData());
                    if (timelinePanel != null) {
                        timelinePanel.updateTimeline(
                            waveformViewModel.getAudioData().getDurationSeconds(),
                            waveformViewModel.getAudioData().getSampleRate()
                        );
                    }
                    // Auto-scroll to show the latest part
                    scrollToLatest(waveformPanel);
                }
            }
        });
        recordingWaveformTimer.start();

        recordButton.addActionListener(e -> {
            // TODO: properly implement recording, stopping, saving
            if (fileDAO.getRecorder().isRecording()) {
                stopRecording.execute();
                // named cache due to the temporary nature of the file
                String outputPath = "cache.wav";
                saveRecording.execute(new SaveRecordingID(outputPath));
                System.out.println("Recording Ended");

                // Automatically load and display the saved recording
                File savedFile = new File(outputPath);
                if (savedFile.exists() && processAudioFileUseCase != null) {
                    ProcessAudioFileID inputData = new ProcessAudioFileID(savedFile);
                    processAudioFileUseCase.execute(inputData);
                    // Set current audio source path for playback
                    currentAudioSourcePath = savedFile.getAbsolutePath();
                    // Ensure scroll pane is updated after loading - force revalidation
                    SwingUtilities.invokeLater(() -> {
                        if (waveformScrollPane != null && waveformPanel != null) {
                            // Force the panel to update its size
                            waveformPanel.revalidate();
                            // Force the scroll pane to recognize the new size
                            waveformScrollPane.revalidate();
                            waveformScrollPane.repaint();
                            // Force update the scrollbar
                            JViewport viewport = waveformScrollPane.getViewport();
                            if (viewport != null && waveformPanel.getPreferredSize().width > viewport.getWidth()) {
                                waveformScrollPane.getHorizontalScrollBar().setEnabled(true);
                                waveformScrollPane.getHorizontalScrollBar().setVisible(true);
                            }
                        }
                    });
                }
            } else {
                startRecording.execute();
                // Clear previous waveform when starting new recording
                if (waveformPanel != null) {
                    waveformPanel.updateWaveform(null);
                }
            }

            if (fileDAO.getRecorder().isRecording()) {
                recordButton.setText("Stop Recording");
            } else {
                recordButton.setText("Start Recording");
            }
        });

        return this;
    }

    /**
     * Scrolls the scroll pane to show the latest part of the waveform during recording.
     * Only scrolls after 30 seconds of recording.
     */
    private void scrollToLatest(WaveformPanel panel) {
        Container parent = panel.getParent();
        if (parent instanceof JViewport && waveformViewModel.getAudioData() != null) {
            JViewport viewport = (JViewport) parent;
            int sampleRate = waveformViewModel.getAudioData().getSampleRate();
            // Account for 256x downsampling
            int samplesIn30Seconds = (sampleRate * 30) / 256;
            int widthFor30Seconds = samplesIn30Seconds / 8;

            int totalSamples = waveformViewModel.getAudioData().getAmplitudeSamples().length;

            // During recording: scroll to show the latest part continuously
            // Calculate the x position of the latest sample
            int latestSampleX = (int) (totalSamples * ((double) widthFor30Seconds / samplesIn30Seconds));
            int viewportWidth = viewport.getWidth();

            // Scroll so the latest part is visible, but keep it smooth
            if (totalSamples > samplesIn30Seconds) {
                // After 30 seconds: scroll to show the latest part
                // Position viewport so latest sample is near the right edge
                int targetX = Math.max(0, latestSampleX - viewportWidth + 50); // 50px padding from right edge
                viewport.setViewPosition(new Point(targetX, 0));
            } else {
                // Before 30 seconds: stay at the beginning
                viewport.setViewPosition(new Point(0, 0));
            }
        }
    }

    public AppBuilder addFingerprintUseCase() {
        final FingerprinterIB fingerprinterInteractor = new FingerprinterInteractor(fileDAO);

        JButton fingerprintButton = new JButton("Fingerprint");
        fingerprintButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(fingerprintButton);
        fingerprintButton.addActionListener(e -> {
            song.setFingerprint(fingerprinterInteractor.execute().getFingerprint());
            System.out.println(song.getFingerprint()); //TODO: remove this once done with debugging
        });
        return this;
    }

    public AppBuilder addIdentifyUseCase() {
        JButton identifyButton = new JButton("Identify");
        identifyButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(identifyButton);

        // TODO: add UI elements to display the Identify information
//        JTextArea songTitle = new JTextArea("Song: ");
//        songTitle.setPreferredSize(new Dimension(200, 200));
//        infoPanel.add(songTitle);
//
//        JTextArea songArtist = new JTextArea("Artist: ");
//        songArtist.setPreferredSize(new Dimension(200, 200));
//        infoPanel.add(songArtist);

        final IdentifyInteractor identifyInteractor = new IdentifyInteractor(song);

        identifyButton.addActionListener(e -> {
            //TODO: handle errors and failure case
            identifyInteractor.identify((int)fileDAO.getAudioRecording().getDurationSeconds());
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
