package org.wavelabs.soundscope.app;

import org.wavelabs.soundscope.view.MainWindow;

import javax.swing.*;

/**
 * Main application entry point for Soundscope.
 * 
 * <p>This class is responsible for wiring together all layers following Clean Architecture principles.
 * It instantiates components from each layer and connects them according to the dependency rule:
 * <ul>
 *   <li>Frameworks & Drivers: MainWindow (View), JavaSoundAudioFileGateway (Data Access)</li>
 *   <li>Interface Adapters: MainController, WaveformPresenter, WaveformViewModel</li>
 *   <li>Application Business Rules: ProcessAudioFileUseCaseInteractor</li>
 *   <li>Enterprise Business Rules: AudioData (Domain Entity)</li>
 * </ul>
 * 
 * <p>The main method sets up the system look and feel, creates all necessary components,
 * wires them together, and displays the main application window.
 */
public class Main {
    
    /**
     * Main entry point for the Soundscope application.
     * 
     * <p>Initializes the UI look and feel, creates and wires all Clean Architecture
     * components, and displays the main application window.
     * 
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            AppBuilder appBuilder = new AppBuilder();
            MainWindow mainWindow = appBuilder.build();
            mainWindow.setVisible(true);
        });
    }
}
