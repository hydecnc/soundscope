package org.wavelabs.soundscope.app;

import javax.swing.JFrame;

/**
 * Entry point for the SoundScope application.
 *
 * <p>This class configures and launches the desktop application responsible for
 * recording audio, processing it, generating fingerprints, and identifying
 * songs. It uses an {@link AppBuilder} to assemble all UI components and
 * use-case interactors according to the principles of Clean Architecture.</p>
 *
 * <p>The constructed {@link JFrame} represents the main application window,
 * which includes controls for recording, playback, waveform visualization,
 * fingerprinting, and song identification. After construction, the window is
 * centered on the screen and displayed to the user.</p>
 */
public class Main {

    /**
     * Application entry point.
     *
     * <p>This method orchestrates the creation of the application by invoking
     * the {@link AppBuilder}. The builder registers and wires together all
     * primary features, including:</p>
     *
     * <ul>
     *   <li>audio recording (start/stop)</li>
     *   <li>saving audio files</li>
     *   <li>audio playback</li>
     *   <li>waveform visualization</li>
     *   <li>audio processing</li>
     *   <li>audio fingerprint generation</li>
     *   <li>song identification via fingerprint matching</li>
     * </ul>
     *
     * <p>Once all components are added, {@code build()} returns the main
     * {@link JFrame}. The frame is then packed, centered, and made visible.</p>
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        final AppBuilder appBuilder = new AppBuilder();
        final JFrame application = appBuilder
                .addMainView()
                .addFileSaveUseCase()
                .addPlayUseCase()
                .addStartRecordUseCase()
                .addStopRecordUseCase()
                .addDisplayRecordingWaveformUseCase()
                .addProcessAudioFileUseCase()
                .addFingerprintUseCase()
                .addIdentifyUseCase()
                .build();
        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}

