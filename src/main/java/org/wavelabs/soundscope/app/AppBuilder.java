package org.wavelabs.soundscope.app;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


public class AppBuilder {
    private final JPanel mainPanel = new JPanel();
    private final JPanel mainButtonPanel = new JPanel();
    private final JPanel titlePanel = new JPanel();

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

    public AppBuilder addRecordUseCase() {
        JButton fingerprintButton = new JButton("Record");
        fingerprintButton.setPreferredSize(new Dimension(400, 200));
        mainButtonPanel.add(fingerprintButton);
        fingerprintButton.addActionListener(e -> {
            // TODO: implement this method hopefully
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

