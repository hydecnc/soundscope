package org.wavelabs.soundscope.interface_adapter.visualize_waveform;
import org.wavelabs.soundscope.view.MainWindow;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileInputBoundary;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileInputData;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Controller for handling user input and coordinating use cases.
 * 
 * <p>Implements Use Case 5: Visualize Waveform. This controller is part of the
 * Interface Adapters layer and is responsible for:
 * <ul>
 *   <li>Receiving user input from the View</li>
 *   <li>Creating Input Data objects</li>
 *   <li>Invoking the appropriate Use Case through the Input Boundary</li>
 *   <li>Observing the ViewModel and updating the View accordingly</li>
 * </ul>
 * 
 * <p>The controller observes the ViewModel and automatically updates the View
 * when the ViewModel changes, ensuring the UI stays synchronized with the
 * application state.
 */
public class MainController {
    private final MainWindow mainWindow;
    private final ProcessAudioFileInputBoundary processAudioFileInputBoundary;
    private final WaveformViewModel viewModel;
    private boolean isRecording = false;
    
    /**
     * Constructs a MainController with the specified dependencies.
     * 
     * @param mainWindow The main application window
     * @param processAudioFileInputBoundary The use case interactor for processing audio files
     * @param viewModel The ViewModel to observe and update
     */
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
    
    /**
     * Starts a timer to periodically observe the ViewModel and update the View.
     * This ensures the UI stays synchronized with the ViewModel state.
     */
    private void startViewModelObserver() {
        Timer timer = new Timer(100, e -> updateViewFromViewModel());
        timer.start();
    }
    
    /**
     * Updates the View based on the current state of the ViewModel.
     * This method is called periodically by the observer timer.
     */
    private void updateViewFromViewModel() {
        if (viewModel.getAudioData() != null) {
            mainWindow.getWaveformPanel().updateWaveform(viewModel.getAudioData());
        }
        mainWindow.getBottomControlPanel().setOutputText(viewModel.getOutputText());
    }
    
    /**
     * Sets up event handlers for all UI components.
     * Connects button actions to their corresponding handler methods.
     */
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
     * 
     * <p>Implements Main Flow of Use Case 5. Displays a file chooser dialog
     * filtered to WAV files only. When a file is selected, processes it through
     * the use case interactor.
     */
    private void onOpenFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Audio File");
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "WAV Audio Files", "wav");
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
     * 
     * <p>Creates Input Data from the selected file and invokes the Use Case
     * through the Input Boundary. The use case will process the file and
     * update the ViewModel, which will trigger a View update.
     * 
     * @param file The audio file to process
     */
    private void processAudioFile(File file) {
        viewModel.setOutputText("Processing audio file: " + file.getName() + "...");
        
        ProcessAudioFileInputData inputData = new ProcessAudioFileInputData(file);
        processAudioFileInputBoundary.execute(inputData);
    }
    
    /**
     * Handles the save file action.
     * Currently displays a placeholder message.
     */
    private void onSaveFile() {
        viewModel.setOutputText("File saved successfully.");
    }
    
    /**
     * Handles the generate fingerprint action.
     * Currently displays a placeholder message.
     */
    private void onGenerateFingerprint() {
        viewModel.setOutputText("Fingerprint: abE671deF");
    }
    
    /**
     * Handles the identify song action.
     * Currently displays a placeholder message.
     */
    private void onIdentifySong() {
        viewModel.setOutputText("Most similar to \"Viva La Vida\"");
    }
    
    /**
     * Handles the play audio action.
     * Currently displays a placeholder message.
     */
    private void onPlayAudio() {
        viewModel.setOutputText("Playing audio...");
    }
    
    /**
     * Handles the record audio action.
     * Toggles between recording and stopped states, updating the button text accordingly.
     */
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

