package org.wavelabs.soundscope;

import org.wavelabs.soundscope.model.AudioData;
import org.wavelabs.soundscope.model.AudioProcessor;
import org.wavelabs.soundscope.view.MainWindow;
import org.wavelabs.soundscope.view.components.StyledButton;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

/**
 * Main application entry point and controller.
 * Handles application initialization and event handling.
 * Implements Use Case 5: Visualize Waveform
 */
public class Main {
    private MainWindow mainWindow;
    private AudioProcessor audioProcessor;
    private AudioData currentAudioData;
    private boolean isRecording = false;
    
    public Main() {
        initializeUI();
        initializeAudioProcessor();
        setupEventHandlers();
    }
    
    private void initializeUI() {
        mainWindow = new MainWindow();
    }
    
    private void initializeAudioProcessor() {
        audioProcessor = new AudioProcessor();
    }
    
    private void setupEventHandlers() {
        // Top toolbar buttons
        mainWindow.getTopToolbar().getOpenButton()
            .addActionListener(e -> onOpenFile());
        mainWindow.getTopToolbar().getSaveButton()
            .addActionListener(e -> onSaveFile());
        mainWindow.getTopToolbar().getFingerprintButton()
            .addActionListener(e -> onGenerateFingerprint());
        mainWindow.getTopToolbar().getIdentifyButton()
            .addActionListener(e -> onIdentifySong());
        
        // Bottom control buttons
        mainWindow.getBottomControlPanel().getPlayButton()
            .addActionListener(e -> onPlayAudio());
        mainWindow.getBottomControlPanel().getRecordButton()
            .addActionListener(e -> onRecordAudio());
    }
    
    /**
     * Handles file opening and waveform visualization.
     * Implements Main Flow of Use Case 5.
     */
    private void onOpenFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Audio File");
        
        // Set file filter for MP3 files only
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "MP3 Audio Files", "mp3");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        int result = fileChooser.showOpenDialog(mainWindow);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processAudioFile(selectedFile);
        }
    }
    
    /**
     * Processes the selected audio file and displays its waveform.
     * Handles error cases as specified in Alternative Flow.
     */
    private void processAudioFile(File file) {
        try {
            // Show loading message
            mainWindow.getBottomControlPanel().setOutputText(
                "Processing audio file: " + file.getName() + "...");
            
            // Process audio file (reads audio data and extracts amplitude samples)
            AudioData audioData = audioProcessor.processAudioFile(file);
            currentAudioData = audioData;
            
            // Display waveform (time vs. amplitude) with scrolling support
            mainWindow.getWaveformPanel().updateWaveform(audioData);
            
            // Show success message with file info
            String duration = String.format("%.2f", audioData.getDurationSeconds());
            mainWindow.getBottomControlPanel().setOutputText(
                "File loaded: " + file.getName() + "<br>" +
                "Duration: " + duration + "s | " +
                "Sample Rate: " + audioData.getSampleRate() + " Hz | " +
                "Channels: " + audioData.getChannels());
            
        } catch (UnsupportedAudioFileException e) {
            // Alternative Flow: Unsupported Audio Format
            handleUnsupportedFormatError(file, e);
        } catch (IOException e) {
            // Alternative Flow: Corrupted file or read error
            handleCorruptedFileError(file, e);
        } catch (Exception e) {
            // Generic error handling
            mainWindow.getBottomControlPanel().setOutputText(
                "Error: " + e.getMessage());
            JOptionPane.showMessageDialog(
                mainWindow,
                "An unexpected error occurred: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Handles unsupported audio format error (Alternative Flow).
     */
    private void handleUnsupportedFormatError(File file, UnsupportedAudioFileException e) {
        String errorMessage = "Unsupported audio format.<br>" +
            "Please use MP3 format only.<br>" +
            "File: " + file.getName();
        
        mainWindow.getBottomControlPanel().setOutputText(errorMessage);
        
        JOptionPane.showMessageDialog(
            mainWindow,
            "Unsupported Audio Format\n\n" +
            "The selected file format is not supported.\n" +
            "Please select an MP3 file.\n\n" +
            "File: " + file.getName() + "\n" +
            "Error: " + e.getMessage(),
            "Unsupported Format",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Handles corrupted file error (Alternative Flow).
     */
    private void handleCorruptedFileError(File file, IOException e) {
        String errorMessage = "File appears to be corrupted or cannot be read.<br>" +
            "Please try a different file or re-record.";
        
        mainWindow.getBottomControlPanel().setOutputText(errorMessage);
        
        JOptionPane.showMessageDialog(
            mainWindow,
            "Corrupted File\n\n" +
            "The selected file cannot be read or appears to be corrupted.\n" +
            "Please try:\n" +
            "• Selecting a different file\n" +
            "• Re-recording the audio\n\n" +
            "File: " + file.getName() + "\n" +
            "Error: " + e.getMessage(),
            "File Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    private void onSaveFile() {
        mainWindow.getBottomControlPanel().setOutputText("File saved successfully.");
        // TODO: Implement file saving logic
    }
    
    private void onGenerateFingerprint() {
        mainWindow.getBottomControlPanel().setOutputText("Fingerprint: abE671deF");
        // TODO: Implement fingerprint generation logic
    }
    
    private void onIdentifySong() {
        mainWindow.getBottomControlPanel().setOutputText("Most similar to \"Viva La Vida\"");
        // TODO: Implement song identification logic
    }
    
    private void onPlayAudio() {
        mainWindow.getBottomControlPanel().setOutputText("Playing audio...");
        // TODO: Implement audio playback logic
    }
    
    /**
     * Handles recording audio. After recording stops, visualizes the waveform.
     */
    private void onRecordAudio() {
        StyledButton recordButton = mainWindow.getBottomControlPanel().getRecordButton();
        
        if (!isRecording) {
            // Start recording
            isRecording = true;
            recordButton.setText("⏹ Stop");
            mainWindow.getBottomControlPanel().setOutputText("Recording...");
            // TODO: Implement recording start logic
        } else {
            // Stop recording
            isRecording = false;
            recordButton.setText("● Record");
            mainWindow.getBottomControlPanel().setOutputText("Recording stopped.");
            
            // TODO: After recording is implemented, process the recorded audio:
            // File recordedFile = getRecordedFile();
            // if (recordedFile != null) {
            //     processAudioFile(recordedFile);
            // }
        }
    }
    
    public void show() {
        mainWindow.setVisible(true);
    }
    
    public static void main(String[] args) {
        // Set look and feel to system default for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
        }
        
        // Create and show the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.show();
        });
    }
}
