package org.wavelabs.soundscope.app;

import org.wavelabs.soundscope.data_access.JavaSoundAudioFileGateway;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.MainController;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformViewModel;
import org.wavelabs.soundscope.use_case.process_audio_file.AudioFileGateway;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileInputBoundary;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileUseCaseInteractor;
import org.wavelabs.soundscope.view.MainWindow;

/**
 * Application builder that constructs and wires all Clean Architecture components.
 * 
 * <p>This class follows the Clean Architecture pattern by creating and connecting
 * all layers: entity, use_case, interface_adapter, data_access, and view.
 * 
 * <p>Similar to the reference Clean Architecture structure, this builder
 * encapsulates the dependency injection logic.
 */
public class AppBuilder {
    
    /**
     * Builds and returns the main application window with all components wired.
     * 
     * @return The configured MainWindow ready to be displayed
     */
    public MainWindow build() {
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
        
        return mainWindow;
    }
}

