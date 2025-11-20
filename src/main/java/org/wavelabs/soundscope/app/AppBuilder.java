package org.wavelabs.soundscope.app;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.wavelabs.soundscope.data_access.FileDAO;
import org.wavelabs.soundscope.data_access.JavaSoundAudioFileGateway;
import org.wavelabs.soundscope.data_access.JavaSoundPlaybackGateway;
import org.wavelabs.soundscope.infrastructure.ByteArrayFileSaver;
import org.wavelabs.soundscope.infrastructure.JavaMicRecorder;
import org.wavelabs.soundscope.interface_adapter.DummyPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformPresenter;
import org.wavelabs.soundscope.interface_adapter.visualize_waveform.WaveformViewModel;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecording;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingIB;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingID;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingOB;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFile;
import org.wavelabs.soundscope.use_case.process_audio_file.ProcessAudioFileID;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecording;
import org.wavelabs.soundscope.use_case.save_recording.SaveRecordingID;
import org.wavelabs.soundscope.use_case.start_recording.StartRecording;
import org.wavelabs.soundscope.use_case.stop_recording.StopRecording;
import org.wavelabs.soundscope.view.components.WaveformPanel;


public class AppBuilder {
    private final JPanel mainPanel = new JPanel();
    private final JPanel mainButtonPanel = new JPanel();
    private final JPanel titlePanel = new JPanel();
    private WaveformPanel waveformPanel;
    private WaveformViewModel waveformViewModel;
    private ProcessAudioFile processAudioFileUseCase;
    private PlayRecordingIB playRecordingUseCase;
    private JButton playPauseButton;
    private String currentAudioSourcePath;

    public AppBuilder() {
        mainButtonPanel.setLayout(new BoxLayout(mainButtonPanel, BoxLayout.X_AXIS));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(titlePanel);
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
        waveformPanel = new WaveformPanel();
        waveformViewModel = new WaveformViewModel();

        WaveformPresenter presenter = new WaveformPresenter(waveformViewModel);
        JavaSoundAudioFileGateway gateway = new JavaSoundAudioFileGateway();
        processAudioFileUseCase = new ProcessAudioFile(gateway, presenter);

        mainPanel.add(waveformPanel);
        mainPanel.add(mainButtonPanel);

        javax.swing.Timer timer = new javax.swing.Timer(100, e -> {
            if (waveformViewModel.getAudioData() != null) {
                waveformPanel.updateWaveform(waveformViewModel.getAudioData());
            }
            if (playRecordingUseCase != null && playPauseButton != null) {
                String desired = playRecordingUseCase.isPlaying() ? "Pause" : "Play";
                if (!desired.equals(playPauseButton.getText())) {
                    playPauseButton.setText(desired);
                }
            }
        });
        timer.start();

        return this;
    }

    public AppBuilder addOpenFileUseCase() {
        JButton openButton = new JButton("Open");
        openButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(openButton);

        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Audio File");

            FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV Audio Files", "wav");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);

            int result = fileChooser.showOpenDialog(mainPanel);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (processAudioFileUseCase != null) {
                    ProcessAudioFileID inputData = new ProcessAudioFileID(selectedFile);
                    processAudioFileUseCase.execute(inputData);
                }
                currentAudioSourcePath = selectedFile.getAbsolutePath();
                if (playRecordingUseCase != null) {
                    playRecordingUseCase.stop();
                }
                if (playPauseButton != null) {
                    playPauseButton.setText("Play");
                }
            }
        });

        return this;
    }

    public AppBuilder addFileSaveUseCase() {
        JButton saveAsButton = new JButton("Save As");
        saveAsButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(saveAsButton);
        saveAsButton.addActionListener(e -> {
            // TODO: implement this method hopefully
        });

        return this;
    }

    public AppBuilder addPlayUseCase() {
        if (playRecordingUseCase == null) {
            playRecordingUseCase = new PlayRecording(new JavaSoundPlaybackGateway(), new PlayRecordingOB() {});
        }

        playPauseButton = new JButton("Play");
        playPauseButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(playPauseButton);
        playPauseButton.addActionListener(e -> {
            if (currentAudioSourcePath == null || currentAudioSourcePath.isBlank()) {
                JOptionPane.showMessageDialog(mainPanel, "Please open or record audio before playing.",
                        "No audio selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (playRecordingUseCase.isPlaying()) {
                    playRecordingUseCase.pause();
                    playPauseButton.setText("Play");
                } else {
                    if (playRecordingUseCase.getTotalFrames() == playRecordingUseCase.getFramesPlayed()) {
                        playRecordingUseCase.play(new PlayRecordingID(currentAudioSourcePath, true));
                    } else {
                        playRecordingUseCase.play(new PlayRecordingID(currentAudioSourcePath, false));
                        playPauseButton.setText("Pause");
                    }
                }
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(mainPanel, ex.getMessage(), "Playback error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return this;
    }

    public AppBuilder addRecordUseCase() {
        JButton fingerprintButton = new JButton("Start Recording");
        fingerprintButton.setPreferredSize(new Dimension(200, 200));
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
            if (fileDAO.getRecorder().isRecording()) {
                stopRecording.execute();
                saveRecording.execute(new SaveRecordingID("./output.wav"));
                System.out.println("Recording Ended");
            } else {
                startRecording.execute();
            }

            if (fileDAO.getRecorder().isRecording()) {
                fingerprintButton.setText("Stop Recording");
            } else {
                fingerprintButton.setText("Start Recording");
            }
        });

        return this;
    }

    public AppBuilder addFingerprintUseCase() {
        JButton fingerprintButton = new JButton("Fingerprint");
        fingerprintButton.setPreferredSize(new Dimension(200, 200));
        mainButtonPanel.add(fingerprintButton);
        fingerprintButton.addActionListener(e -> {
            // TODO: implement this method hopefully
        });

        return this;
    }

    public AppBuilder addIdentifyUseCase() {
        JButton identifyButton = new JButton("Identify");
        identifyButton.setPreferredSize(new Dimension(200, 200));
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

