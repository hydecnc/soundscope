package org.wavelabs.soundscope;

import java.awt.*;
import java.io.*;
import javax.swing.*;

public class MainUserGUI {
    public  static void main(String[] args) {
        JFrame frame = new JFrame("Soundscope");
        frame.setMinimumSize(new java.awt.Dimension(600, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // main title at the top of the window
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Soundscope");
        titleLabel.setFont(new Font("Sans Serif", Font.BOLD, 36));
        titlePanel.add(titleLabel);

        // individual buttons - all 4 of them
        JPanel mainButtonPanel = new JPanel();
        mainButtonPanel.setLayout(new BoxLayout(mainButtonPanel, BoxLayout.X_AXIS));

        /* For the action listeners,
        Intellij has advised me to use lambda functions. So that's what I did.
        Because it was easier.
         */

        // Open button panel
        JButton openButton = new JButton("Open");
        openButton.setPreferredSize(new Dimension(400, 200));
        mainButtonPanel.add(openButton);

        openButton.addActionListener(e -> {
            // Todo: implement this method hopefully
        });

        // Save As button panel
        JButton saveAsButton = new JButton("Save As");
        saveAsButton.setPreferredSize(new Dimension(400, 200));
        mainButtonPanel.add(saveAsButton);
        saveAsButton.addActionListener(e -> {
            // Todo: implement this method hopefully
        });

        // Fingerprint button panel
        JButton fingerprintButton = new JButton("Fingerprint");
        fingerprintButton.setPreferredSize(new Dimension(400, 200));
        mainButtonPanel.add(fingerprintButton);
        fingerprintButton.addActionListener(e -> {
            // Todo: implement this method hopefully
        });

        // Indentify Button Panel
        JButton identifyButton = new JButton("Identify");
        identifyButton.setPreferredSize(new Dimension(400, 200));
        mainButtonPanel.add(identifyButton);
        identifyButton.addActionListener(e -> {
            // Todo: implement this method hopefully
        });


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(titlePanel);
        mainPanel.add(mainButtonPanel);

        frame.add(mainPanel);
    }
}
