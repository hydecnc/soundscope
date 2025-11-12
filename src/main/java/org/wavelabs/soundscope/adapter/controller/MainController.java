package org.wavelabs.soundscope.adapter.controller;

import org.wavelabs.soundscope.adapter.viewmodel.WaveformViewModel;
import org.wavelabs.soundscope.framework.ui.MainWindow;
import org.wavelabs.soundscope.usecase.ProcessAudioFileInputBoundary;
import org.wavelabs.soundscope.usecase.ProcessAudioFileInputData;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Controller for handling user input and coordinating use cases.
 * Implements Use Case 5: Visualize Waveform
 * Receives input from View and invokes the Input Boundary.
 */
public class MainController {
    private final MainWindow mainWindow;
    private final ProcessAudioFileInputBoundary processAudioFileInputBoundary;
    private final WaveformViewModel viewModel;
    private boolean isRecording = false;
    
    public MainController(
            MainWindow mainWindow,
            ProcessAudioFileInputBoundary processAudioFileInputBoundary,
            WaveformViewModel viewModel) {
        this.mainWindow = mainWindow;
        this.processAudioFileInputBoundary = processAudioFileInputBoundary;
        this.viewModel = viewModel;
        setupEventHandlers();
        startViewModelObserver();
    }
    
    private void startViewModelObserver() {
        Timer timer = new Timer(100, e -> updateViewFromViewModel());
        timer.start();
    }
    
    private void updateViewFromViewModel() {
        if (viewModel.getAudioData() != null) {
            mainWindow.getWaveformPanel().updateWaveform(viewModel.getAudioData());
        }
        mainWindow.getBottomControlPanel().setOutputText(viewModel.getOutputText());
    }
    
    private void setupEventHandlers() {
        mainWindow.getTopToolbar().getOpenButton()
            .addActionListener(e -> onOpenFile());
        mainWindow.getTopToolbar().getSaveButton()
            .addActionListener(e -> onSaveFile());
        mainWindow.getTopToolbar().getFingerprintButton()
            .addActionListener(e -> onGenerateFingerprint());
        mainWindow.getTopToolbar().getIdentifyButton()
            .addActionListener(e -> onIdentifySong());
        
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
     * Creates Input Data and invokes the Input Boundary (Use Case).
     */
    private void processAudioFile(File file) {
        viewModel.setOutputText("Processing audio file: " + file.getName() + "...");
        
        ProcessAudioFileInputData inputData = new ProcessAudioFileInputData(file);
        processAudioFileInputBoundary.execute(inputData);
    }
    
    private void onSaveFile() {
        viewModel.setOutputText("File saved successfully.");
    }
    
    private void onGenerateFingerprint() {
        viewModel.setOutputText("Fingerprint: abE671deF");
    }
    
    private void onIdentifySong() {
        viewModel.setOutputText("Most similar to \"Viva La Vida\"");
    }
    
    private void onPlayAudio() {
        viewModel.setOutputText("Playing audio...");
    }
    
    private void onRecordAudio() {
        var recordButton = mainWindow.getBottomControlPanel().getRecordButton();
        
        if (!isRecording) {
            isRecording = true;
            recordButton.setText("⏹ Stop");
            viewModel.setOutputText("Recording...");
        } else {
            isRecording = false;
            recordButton.setText("● Record");
            viewModel.setOutputText("Recording stopped.");
        }
    }
}

