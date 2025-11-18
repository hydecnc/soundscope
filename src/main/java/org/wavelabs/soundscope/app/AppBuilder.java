package org.wavelabs.soundscope.app;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.wavelabs.soundscope.data_access.FileDAO;
import org.wavelabs.soundscope.infrastructure.ByteArrayFileSaver;
import org.wavelabs.soundscope.infrastructure.JavaMicRecorder;
import org.wavelabs.soundscope.interface_adapter.DummyPresenter;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecording;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingID;
import org.wavelabs.soundscope.use_case.start_recording.StartRecording;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecording;


public class AppBuilder {
    private final JPanel mainPanel = new JPanel();
    private final JPanel mainButtonPanel = new JPanel();
    private final JPanel titlePanel = new JPanel();
    private static boolean playing = false; // TODO: decide if it's worth moving this into the play use case

    public AppBuilder() {
        mainButtonPanel.setLayout(new BoxLayout(mainButtonPanel, BoxLayout.X_AXIS));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(titlePanel);
        mainPanel.add(mainButtonPanel);

    }

    public AppBuilder addTitle() {
        JLabel titleLabel = new JLabel("Soundscope");
        titleLabel.setFont(new Font("Sans Serif", Font.BOLD, 36));
        titlePanel.add(titleLabel);

        return this;
    }

    // Use case view views & view models
    // NOTE: Currently, this is missing controllers, presenters, view models, etc.
    public AppBuilder addWaveFormView() {
        return this;
    }

    public AppBuilder addOpenFileUseCase() {
        JButton openButton = new JButton("Open");
        openButton.setPreferredSize(new Dimension(400, 200));
        mainButtonPanel.add(openButton);

        openButton.addActionListener(e -> {
            // TODO: implement this method hopefully
        });

        return this;
    }

    public AppBuilder addFileSaveUseCase() {
        JButton saveAsButton = new JButton("Save As");
        saveAsButton.setPreferredSize(new Dimension(400, 200));
        mainButtonPanel.add(saveAsButton);
        saveAsButton.addActionListener(e -> {
            // TODO: implement this method hopefully
        });

        return this;
    }

    public AppBuilder addPlayUseCase() {
        JButton playPauseButton = new JButton("Play");

        playPauseButton.setPreferredSize(new Dimension(400, 200));
        mainButtonPanel.add(playPauseButton);
        playPauseButton.addActionListener(e -> {
            playing = !playing;
            if(playing){
                playPauseButton.setText("Pause");
            }else{
                playPauseButton.setText("Play");
            }

            // TODO: implement correct use case calls
        });

        return this;
    }

    public AppBuilder addRecordUseCase() {
        JButton fingerprintButton = new JButton("Start Recording");
        fingerprintButton.setPreferredSize(new Dimension(400, 200));
        mainButtonPanel.add(fingerprintButton);

        FileDAO fileDAO = new FileDAO();
        fileDAO.setFileSaver(new ByteArrayFileSaver());
        fileDAO.setRecorder(new JavaMicRecorder());

        DummyPresenter dummyPresenter = new DummyPresenter();

        StartRecording startRecording = new StartRecording(fileDAO, dummyPresenter);
        StopRecording stopRecording = new StopRecording(fileDAO, dummyPresenter);
        SaveRecording saveRecording = new SaveRecording(fileDAO, dummyPresenter);

        fingerprintButton.addActionListener(e -> {
            // TODO: properly implement recording, stopping, saving
            if(fileDAO.getRecorder().isRecording()){
                stopRecording.execute();
                saveRecording.execute(new SaveRecordingID("./output.wav"));
                System.out.println("Recording Ended");
            }else{
                startRecording.execute();
            }

            if(fileDAO.getRecorder().isRecording()){
                fingerprintButton.setText("Stop Recording");
            }else{
                fingerprintButton.setText("Start Recording");
            }
        });

        return this;
    }

    public AppBuilder addFingerprintUseCase() {
        JButton fingerprintButton = new JButton("Fingerprint");
        fingerprintButton.setPreferredSize(new Dimension(400, 200));
        mainButtonPanel.add(fingerprintButton);
        fingerprintButton.addActionListener(e -> {
            // TODO: implement this method hopefully
        });

        return this;
    }

    public AppBuilder addIdentifyUseCase() {
        JButton identifyButton = new JButton("Identify");
        identifyButton.setPreferredSize(new Dimension(400, 200));
        mainButtonPanel.add(identifyButton);
        identifyButton.addActionListener(e -> {
            // TODO: implement this method hopefully
        });

        return this;
    }


    public JFrame build() {
        final JFrame application = new JFrame("Soundscope");
        application.setMinimumSize(new Dimension(600, 600));
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.setContentPane(mainPanel);

        // NOTE: Consider adding view manager model
        return application;
    }
}

