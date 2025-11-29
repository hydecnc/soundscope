package org.wavelabs.soundscope.view;

import org.jetbrains.annotations.NotNull;
import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.interface_adapter.MainState;
import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintController;
import org.wavelabs.soundscope.interface_adapter.identify.IdentifyController;
import org.wavelabs.soundscope.interface_adapter.play_recording.PlayRecordingController;
import org.wavelabs.soundscope.interface_adapter.process_audio_file.ProcessAudioFileController;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveRecordingController;
import org.wavelabs.soundscope.interface_adapter.start_recording.StartRecordingController;
import org.wavelabs.soundscope.interface_adapter.stop_recording.StopRecordingController;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.DisplayRecordingWaveformController;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformViewModel;
import org.wavelabs.soundscope.view.components.TimelinePanel;
import org.wavelabs.soundscope.view.components.WaveformPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainView extends JPanel implements ActionListener, PropertyChangeListener {
    private final MainViewModel mainViewModel;

    //Controllers for the various use cases
    private FingerprintController fingerprintController;
    private DisplayRecordingWaveformController waveformController; //TODO: set this up; is it necessary?
    private IdentifyController identifyController;
    private PlayRecordingController playRecordingController;
    private ProcessAudioFileController processAudioFileController;
    private SaveRecordingController saveRecordingController;
    private StartRecordingController startRecordingController;
    private StopRecordingController stopRecordingController;
    private DisplayRecordingWaveformController displayRecordingWaveformController;

    //Button code
    private final JPanel buttonPanel = new JPanel();
    private final JButton openButton, saveAsButton, playPauseButton,
            recordButton, fingerprintButton, identifyButton;

    //Waveform panel code
    private final WaveformPanel waveformPanel;
    private final TimelinePanel timelinePanel;
    private final JScrollPane waveformScrollPane;
    private final JScrollPane timelineScrollPane;
    private final WaveformViewModel waveformViewModel;

    //Info panel
    private final JPanel infoPanel = new JPanel();
    private final JTextField fingerprintInfo, songTitleInfo, albumInfo;

    //TODO: migrate App Builder stuff here
    public MainView(MainViewModel mainViewModel, WaveformViewModel waveformViewModel) {
        this.mainViewModel = mainViewModel;
        this.waveformViewModel = waveformViewModel; //TODO: rename waveform view model to something else since
                                                // it doesn't extend ViewModel and therefore isn't a View Model
        mainViewModel.addPropertyChangeListener(this);

        //Sets the title
        final JLabel title = new JLabel(MainViewModel.TITLE);
        title.setFont(new Font("Sans Serif", Font.BOLD, 36));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sets up waveform information
        waveformPanel = new WaveformPanel();
        timelinePanel = new TimelinePanel();
        waveformScrollPane = new JScrollPane(waveformPanel);
        timelineScrollPane = new JScrollPane(timelinePanel);
        setupScrollPanes();

        // Create container for timeline and waveform
        JPanel waveformContainer = new JPanel(new BorderLayout());
        waveformContainer.add(timelineScrollPane, BorderLayout.NORTH);
        waveformContainer.add(waveformScrollPane, BorderLayout.CENTER);

        this.add(waveformContainer);

        // Sets up all the buttons
        openButton = getOpenButton();
        saveAsButton = getSaveAsButton();
        playPauseButton = getPlayPauseButton();
        recordButton = getRecordButton();
        identifyButton = getIdentifyButton();
        fingerprintButton = getFingerprintButton();

        buttonPanel.add(openButton);
        buttonPanel.add(saveAsButton);
        buttonPanel.add(playPauseButton);
        buttonPanel.add(recordButton);
        buttonPanel.add(fingerprintButton);
        buttonPanel.add(identifyButton);

        // Sets up info panel
        fingerprintInfo = new JTextField(MainViewModel.FINGERPRINT_INFO_START);
        songTitleInfo = new JTextField(MainViewModel.SONG_TITLE_INFO_START);
        albumInfo = new JTextField(MainViewModel.ALBUM_INFO_START);

        fingerprintInfo.setMinimumSize(MainViewModel.MIN_INFO_DIMENSIONS);
        songTitleInfo.setMinimumSize(MainViewModel.MIN_INFO_DIMENSIONS);
        albumInfo.setMinimumSize(MainViewModel.MIN_INFO_DIMENSIONS);

        fingerprintInfo.setEditable(false);
        songTitleInfo.setEditable(false);
        albumInfo.setEditable(false);

        infoPanel.add(fingerprintInfo);
        infoPanel.add(songTitleInfo);
        infoPanel.add(albumInfo);

        infoPanel.setMaximumSize(MainViewModel.MAX_INFO_PANEL_DIMENSIONS);

        //Sets up main panel
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        this.add(title);
        this.add(waveformContainer);
        this.add(infoPanel);
        this.add(buttonPanel);
    }

    @NotNull
    private void setupScrollPanes() {
        // Sets up and synchronizes scroll panes for timeline and waveform
        timelineScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        timelineScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        timelineScrollPane.setBorder(null);

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


        javax.swing.Timer timer = new javax.swing.Timer(100, e -> {
            if (waveformViewModel.getAudioData() != null) {
                // Get playback position from playback use case (works for both playing and paused)
                double playbackPositionSeconds = 0.0;

                long framesPlayed = mainViewModel.getState().getFramesPlayed();
                int sampleRate = waveformViewModel.getAudioData().getSampleRate();
                if (sampleRate > 0 && framesPlayed > 0) {
                    playbackPositionSeconds = (double) framesPlayed / sampleRate;
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
            String desired = mainViewModel.getState().isPlaying() ? MainViewModel.PAUSE_TEXT : MainViewModel.PLAY_TEXT;
            if (!desired.equals(playPauseButton.getText())) {
                playPauseButton.setText(desired);

            }
        });
        timer.start();
    }

    @NotNull
    private JButton getFingerprintButton(){
        JButton fingerprintButton = new JButton(MainViewModel.FINGERPRINT_TEXT);
        fingerprintButton.setPreferredSize(MainViewModel.DEFAULT_BUTTON_DIMENSIONS);

        fingerprintButton.addActionListener(e -> {
            fingerprintController.execute();

        });

        return fingerprintButton;
    }

    @NotNull
    private JButton getIdentifyButton(){
        JButton identifyButton = new JButton(MainViewModel.IDENTIFY_TEXT);
        identifyButton.setPreferredSize(MainViewModel.DEFAULT_BUTTON_DIMENSIONS);

        identifyButton.addActionListener(e -> {
            identifyController.identify();
        });

        return identifyButton;
    }

    @NotNull
    private JButton getRecordButton(){
        JButton recordButton = new JButton(MainViewModel.RECORD_TEXT);
        recordButton.setPreferredSize(MainViewModel.DEFAULT_BUTTON_DIMENSIONS);
        buttonPanel.add(recordButton);

        // Timer to update waveform during recording
        Timer recordingWaveformTimer = new Timer(50, e -> {
            if (mainViewModel.getState().isRecording()) {
                displayRecordingWaveformController.execute();
                AudioData audioData = waveformViewModel.getAudioData();
                // Force immediate update and auto-scroll to show latest
                if (audioData != null && waveformPanel != null) {
                    waveformPanel.updateWaveform(audioData);
                    if (timelinePanel != null) {
                        timelinePanel.updateTimeline(
                                audioData.getDurationSeconds(),
                                audioData.getSampleRate());
                    }
                    // Auto-scroll to show the latest part
                    waveformPanel.scrollToLatest(audioData);
                }
            }
        });
        recordingWaveformTimer.start();

        recordButton.addActionListener(e -> {
            if (mainViewModel.getState().isRecording()) {
                stopRecordingController.execute();
                mainViewModel.getState().setRecording(false);

                // named cache due to the temporary nature of the file
                String outputPath = "cache.wav"; //TODO: put a better file path here
                saveRecordingController.execute(outputPath);
                System.out.println("Recording Ended");

                // Automatically load and display the saved recording
                File savedFile = new File(outputPath);
                if (savedFile.exists()) {
                    processAudioFileController.execute(savedFile);

                    // Set current audio source path for playback
                    mainViewModel.getState().setCurrentAudioSourcePath(savedFile.getAbsolutePath());

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
                            if (viewport != null
                                    && waveformPanel.getPreferredSize().width > viewport
                                    .getWidth()) {
                                waveformScrollPane.getHorizontalScrollBar().setEnabled(true);
                                waveformScrollPane.getHorizontalScrollBar().setVisible(true);
                            }
                        }
                    });
                }
            } else {
                startRecordingController.execute();
                mainViewModel.getState().setRecording(true);
                // Clear previous waveform when starting new recording
                if (waveformPanel != null) {
                    waveformPanel.updateWaveform(null);
                }
            }

            if (mainViewModel.getState().isRecording()) {
                recordButton.setText(MainViewModel.STOP_RECORDING_TEXT);
            } else {
                recordButton.setText(MainViewModel.RECORD_TEXT);
            }
        });

        return recordButton;
    }

    @NotNull
    private JButton getPlayPauseButton(){
        JButton playPauseButton = new JButton(MainViewModel.PLAY_TEXT);
        playPauseButton.setPreferredSize(MainViewModel.DEFAULT_BUTTON_DIMENSIONS);
        buttonPanel.add(playPauseButton);
        playPauseButton.addActionListener(e -> {
            final String currentAudioSourcePath = mainViewModel.getState().getCurrentAudioSourcePath();

            if (currentAudioSourcePath == null || currentAudioSourcePath.isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Please open or record audio before playing.", "No audio selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (mainViewModel.getState().isPlaying()) {
                    playRecordingController.pause();
                    playPauseButton.setText(MainViewModel.PLAY_TEXT);
                } else {
                    if (mainViewModel.getState().isPlayingFinished()) {
                        playRecordingController.play(currentAudioSourcePath, true);
                    } else {
                        playRecordingController.play(currentAudioSourcePath, false);
                        playPauseButton.setText(MainViewModel.PAUSE_TEXT);
                    }
                }
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Playback error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        return playPauseButton;
    }

    @NotNull
    private JButton getSaveAsButton(){
        JButton saveAsButton = new JButton(MainViewModel.SAVE_AS_TEXT);
        saveAsButton.setPreferredSize(MainViewModel.DEFAULT_BUTTON_DIMENSIONS);
        buttonPanel.add(saveAsButton);
        saveAsButton.addActionListener(e -> {
            saveFileToDirectory();
        });

        return saveAsButton;
    }

    private void saveFileToDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle(MainViewModel.SAVE_AS_FILE_CHOOSER_TITLE);

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

            saveRecordingController.execute(outputFile.getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(this, "No file selection made",
                    "Error", JOptionPane.WARNING_MESSAGE);
            System.out.println("No Selection");
        };
    }

    @NotNull
    private JButton getOpenButton() {
        final JButton openButton;
        openButton = new JButton(MainViewModel.OPEN_TEXT);
        openButton.setPreferredSize(MainViewModel.DEFAULT_BUTTON_DIMENSIONS);
        buttonPanel.add(openButton);

        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(MainViewModel.OPEN_FILE_CHOOSER_TITLE);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV Audio Files", "wav");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);

            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                processAudioFileController.execute(selectedFile);

                // Update chosen file path
                mainViewModel.getState().setCurrentAudioSourcePath(selectedFile.getAbsolutePath());

                // Stop recording
                playRecordingController.stop();
                playPauseButton.setText(MainViewModel.PLAY_TEXT);
            }
        });
        return openButton;
    }

    public void setFingerprintController(FingerprintController fingerprintController) {
        this.fingerprintController = fingerprintController;
    }

    public void setWaveformController(DisplayRecordingWaveformController waveformController) {
        this.waveformController = waveformController;
    }

    public void setIdentifyController(IdentifyController identifyController) {
        this.identifyController = identifyController;
    }

    public void setPlayRecordingController(PlayRecordingController playRecordingController) {
        this.playRecordingController = playRecordingController;
    }

    public void setProcessAudioFileController(ProcessAudioFileController processAudioFileController) {
        this.processAudioFileController = processAudioFileController;
    }

    public void setSaveRecordingController(SaveRecordingController saveRecordingController) {
        this.saveRecordingController = saveRecordingController;
    }

    public void setStartRecordingController(StartRecordingController startRecordingController) {
        this.startRecordingController = startRecordingController;
    }

    public void setStopRecordingController(StopRecordingController stopRecordingController) {
        this.stopRecordingController = stopRecordingController;
    }

    public void setDisplayRecordingWaveformController(DisplayRecordingWaveformController displayRecordingWaveformController) {
        this.displayRecordingWaveformController = displayRecordingWaveformController;
    }

    @Override
    public void actionPerformed(ActionEvent evt) { //TODO: Does anything need to be added here?

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final MainState state = (MainState) evt.getNewValue();

        // If in an error state, we display an error message
        if(state.isErrorState()){
            String errorTitle = MainViewModel.USE_CASE_ERROR_TITLE_MAP.get(evt.getPropertyName());
            if (errorTitle == null) errorTitle = "Unknown Error Type";

            JOptionPane.showMessageDialog(
                    this,
                    state.getErrorMessage(),
                    errorTitle,
                    JOptionPane.ERROR_MESSAGE
            );
            //Clears an error state
            mainViewModel.getState().setErrorState(false);
            return;
        }

        //TODO: implement property change updates from all the other use cases

        if(evt.getPropertyName().equals("playing")){ //Updates play button visual state if song finishes
            if(state.isPlaying()){
                playPauseButton.setText(MainViewModel.PAUSE_TEXT);
            }else{
                playPauseButton.setText(MainViewModel.PLAY_TEXT);
            }
            return;
        }

        if(evt.getPropertyName().equals("recording")){ //Updates recording button visual state if it changes
            if(state.isRecording()){
                recordButton.setText(MainViewModel.STOP_RECORDING_TEXT);
            }else{
                recordButton.setText(MainViewModel.RECORD_TEXT);
            }
            return;
        }

        if(evt.getPropertyName().equals("identify")){
            songTitleInfo.setText(MainViewModel.SONG_TITLE_INFO_START + state.getSongTitle());
            albumInfo.setText(MainViewModel.ALBUM_INFO_START + state.getAlbum());
            return;
        }

        if(evt.getPropertyName().equals("fingerprint")) {
            String fingerprint = state.getFingerprint();
            final int newLength = Math.min(MainViewModel.FINGERPRINT_DISPLAY_LENGTH, fingerprint.length());

            String displayFingerprint = fingerprint.substring(0, newLength);
            if(newLength < fingerprint.length()){
                displayFingerprint += " ...";
            }

            fingerprintInfo.setText(MainViewModel.FINGERPRINT_INFO_START + displayFingerprint);
            return;
        }
    }
}
