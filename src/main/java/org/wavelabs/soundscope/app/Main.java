package org.wavelabs.soundscope.app;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
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
