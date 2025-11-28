package org.wavelabs.soundscope.view;

import org.jetbrains.annotations.NotNull;
import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.interface_adapter.MainState;
import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintController;
import org.wavelabs.soundscope.interface_adapter.play_recording.PlayRecordingController;
import org.wavelabs.soundscope.interface_adapter.process_audio_file.ProcessAudioFileController;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveRecordingController;
import org.wavelabs.soundscope.interface_adapter.start_recording.StartRecordingController;
import org.wavelabs.soundscope.interface_adapter.stop_recording.StopRecordingController;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.DisplayRecordingWaveformController;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.DisplayRecordingWaveformPresenter;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveform;
import org.wavelabs.soundscope.use_case.display_recording_waveform.DisplayRecordingWaveformID;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileID;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingID;
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
    private DisplayRecordingWaveformController waveformController; //TODO: make this class
    private IdentifyController identifyController; //TODO: make this class
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
    private WaveformPanel waveformPanel;
    private TimelinePanel timelinePanel;
    private JScrollPane waveformScrollPane;




    //TODO: migrate App Builder stuff here
    public MainView(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
        mainViewModel.addPropertyChangeListener(this);

        //Sets the title
        final JLabel title = new JLabel(MainViewModel.TITLE);
        title.setFont(new Font("Sans Serif", Font.BOLD, 36));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);


        // Sets up all of the buttons
        //TODO: finish setting up buttons
        openButton = getOpenButton();
        saveAsButton = getSaveAsButton();
        playPauseButton = getPlayPauseButton();
        recordButton = getRecordButton();

        buttonPanel.add(openButton);
        buttonPanel.add(saveAsButton);
        buttonPanel.add(playPauseButton);
        buttonPanel.add(recordButton);
        buttonPanel.add(fingerprintButton);
        buttonPanel.add(identifyButton);

        //TODO: add waveform specific stuff


        this.add(title);
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
                AudioData audioData = waveformViewModel.getAudioData(); //TODO: attach a waveformViewModel here
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
                //TODO: need to be able to listen to the view model's state instead of to the play use case
                if (playRecordingUseCase.isPlaying()) {
                    playRecordingController.pause();
                    playPauseButton.setText(MainViewModel.PLAY_TEXT);
                } else {
                    if (playRecordingUseCase.getTotalFrames() == playRecordingUseCase
                            .getFramesPlayed()) {
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
    public void actionPerformed(ActionEvent evt) { //TODO: finish this

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("file save")) {
            final MainState state = (MainState) evt.getNewValue();
            if(state.isSuccessfulSave()){
                System.out.println("Recording saved");
            }else{
                JOptionPane.showMessageDialog(this, state.getErrorMessage(),
                        "Error during save", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
    }
}
