package org.wavelabs.soundscope.app;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.wavelabs.soundscope.data_access.FileDAO;
import org.wavelabs.soundscope.data_access.JavaSoundAudioFileGateway;
import org.wavelabs.soundscope.data_access.JavaSoundPlaybackGateway;
import org.wavelabs.soundscope.infrastructure.ByteArrayFileSaver;
import org.wavelabs.soundscope.infrastructure.JavaMicRecorder;
import org.wavelabs.soundscope.interface_adapter.DummyPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.DisplayRecordingWaveformPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformViewModel;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveform;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformID;
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
    private DisplayRecordingWaveform displayRecordingWaveformUseCase;
    private FileDAO fileDAO;
    private javax.swing.Timer recordingWaveformTimer;
    private PlayRecordingIB playRecordingUseCase;
    private JButton playPauseButton;
    private String currentAudioSourcePath;

    public AppBuilder() {
        mainButtonPanel.setLayout(new BoxLayout(mainButtonPanel, BoxLayout.X_AXIS));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(titlePanel);
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
        JavaSoundAudioFileGateway gateway = new JavaSoundAudioFileGateway();
        processAudioFileUseCase = new ProcessAudioFile(gateway, presenter);
        
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
                // Get real-time playback position from playback use case
                double playbackPositionSeconds = 0.0;
                if (playRecordingUseCase != null && playRecordingUseCase.isPlaying()) {
                    int framesPlayed = playRecordingUseCase.getFramesPlayed();
                    int sampleRate = waveformViewModel.getAudioData().getSampleRate();
                    if (sampleRate > 0) {
                        playbackPositionSeconds = (double) framesPlayed / sampleRate;
                    }
                }
                
                // Update waveform with playback position
                waveformPanel.updateWaveform(waveformViewModel.getAudioData(), playbackPositionSeconds);
                
                // Update timeline with same data
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
            fileChooser.setDialogTitle("Select Audio File");
            
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "WAV Audio Files", "wav");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            
            int result = fileChooser.showOpenDialog(mainPanel);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (processAudioFileUseCase != null) {
                    ProcessAudioFileID inputData = new ProcessAudioFileID(selectedFile);
                    processAudioFileUseCase.execute(inputData);
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
            // TODO: implement this method hopefully
        });
        return this;
    }

    public AppBuilder addPlayUseCase() {
        if (playRecordingUseCase == null) {
            playRecordingUseCase = new PlayRecording(new JavaSoundPlaybackGateway(), new PlayRecordingOB() {});
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
        JButton fingerprintButton = new JButton("Start Recording");
        fingerprintButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(fingerprintButton);

        fileDAO = new FileDAO();
        fileDAO.setFileSaver(new ByteArrayFileSaver());
        fileDAO.setRecorder(new JavaMicRecorder());

        DummyPresenter dummyPresenter = new DummyPresenter();
        StartRecording startRecording = new StartRecording(fileDAO, dummyPresenter);
        StopRecording stopRecording = new StopRecording(fileDAO, dummyPresenter);
        SaveRecording saveRecording = new SaveRecording(fileDAO, dummyPresenter);
        
        // Set up DisplayRecordingWaveform use case
        DisplayRecordingWaveformPresenter recordingPresenter = 
            new DisplayRecordingWaveformPresenter(waveformViewModel);
        displayRecordingWaveformUseCase = new DisplayRecordingWaveform(fileDAO, recordingPresenter);
        
        // Timer to update waveform during recording
        recordingWaveformTimer = new javax.swing.Timer(50, e -> {
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

        fingerprintButton.addActionListener(e -> {
            // TODO: properly implement recording, stopping, saving
            if (fileDAO.getRecorder().isRecording()) {
                stopRecording.execute();
                String outputPath = "./output.wav";
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
                fingerprintButton.setText("Stop Recording");
            } else {
                fingerprintButton.setText("Start Recording");
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
        JButton fingerprintButton = new JButton("Fingerprint");
        fingerprintButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(fingerprintButton);
        fingerprintButton.addActionListener(e -> {
            // TODO: implement this method hopefully
        });
        return this;
    }

    public AppBuilder addIdentifyUseCase() {
        JButton identifyButton = new JButton("Identify");
        identifyButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(identifyButton);
        identifyButton.addActionListener(e -> {
            // TODO: implement this method hopefully
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
