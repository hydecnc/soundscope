package org.wavelabs.soundscope.view;

import org.jetbrains.annotations.NotNull;
import org.wavelabs.soundscope.interface_adapter.MainState;
import org.wavelabs.soundscope.interface_adapter.MainViewModel;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintController;
import org.wavelabs.soundscope.interface_adapter.play_recording.PlayRecordingController;
import org.wavelabs.soundscope.interface_adapter.process_audio_file.ProcessAudioFileController;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveRecordingController;
import org.wavelabs.soundscope.interface_adapter.save_file.SaveRecordingPresenter;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecording;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingID;

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
    private StartRecordingController startRecordingController; //TODO: make this class
    private StopRecordingController stopRecordingController; //TODO: make this class

    //Button code
    private final JPanel buttonPanel = new JPanel();
    private final JButton openButton, saveAsButton, playPauseButton,
            recordButton, fingerprintButton, identifyButton;


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
