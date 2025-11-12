package org.wavelabs.soundscope;

import org.wavelabs.soundscope.adapter.controller.MainController;
import org.wavelabs.soundscope.adapter.gateway.JavaSoundAudioFileGateway;
import org.wavelabs.soundscope.adapter.presenter.WaveformPresenter;
import org.wavelabs.soundscope.adapter.viewmodel.WaveformViewModel;
import org.wavelabs.soundscope.framework.ui.MainWindow;
import org.wavelabs.soundscope.usecase.AudioFileGateway;
import org.wavelabs.soundscope.usecase.ProcessAudioFileInputBoundary;
import org.wavelabs.soundscope.usecase.ProcessAudioFileUseCaseInteractor;

import javax.swing.*;

/**
 * Main application entry point.
 * Wires together all layers following Clean Architecture principles.
 * Matches the Clean Architecture diagram structure.
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            // Frameworks & Drivers layer: View
            MainWindow mainWindow = new MainWindow();
            
            // Interface Adapters layer: View Model
            WaveformViewModel viewModel = new WaveformViewModel();
            
            // Interface Adapters layer: Presenter (implements Output Boundary)
            WaveformPresenter presenter = new WaveformPresenter(viewModel);
            
            // Frameworks & Drivers layer: Data Access implementation
            AudioFileGateway audioFileGateway = new JavaSoundAudioFileGateway();
            
            // Application Business Rules layer: Use Case Interactor
            // (implements Input Boundary, depends on Output Boundary and Data Access Interface)
            ProcessAudioFileInputBoundary useCaseInteractor = 
                new ProcessAudioFileUseCaseInteractor(audioFileGateway, presenter);
            
            // Interface Adapters layer: Controller (implements Input Boundary)
            new MainController(mainWindow, useCaseInteractor, viewModel);
            
            // Show the application
            mainWindow.setVisible(true);
        });
    }
}
