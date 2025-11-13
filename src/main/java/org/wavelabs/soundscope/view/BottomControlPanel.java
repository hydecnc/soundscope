package org.wavelabs.soundscope.view;
import org.wavelabs.soundscope.view.components.StyledButton;

import javax.swing.*;
import java.awt.*;

/**
 * Bottom control panel containing output label and playback/recording controls.
 * 
 * <p>This class is part of the Frameworks & Drivers layer and provides
 * a panel at the bottom of the main window that displays output messages
 * and contains playback and recording control buttons.
 */
public class BottomControlPanel extends JPanel {
    private final JLabel outputLabel;
    private final StyledButton playButton;
    private final StyledButton recordButton;
    
    /**
     * Constructs a BottomControlPanel with output label and control buttons.
     */
    public BottomControlPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIStyle.Colors.BACKGROUND_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(
            UIStyle.Spacing.BOTTOM_PADDING,
            UIStyle.Spacing.BOTTOM_PADDING,
            UIStyle.Spacing.BOTTOM_PADDING,
            UIStyle.Spacing.BOTTOM_PADDING
        ));
        
        outputLabel = new JLabel();
        outputLabel.setFont(UIStyle.Fonts.DEFAULT);
        outputLabel.setForeground(UIStyle.Colors.TEXT_PRIMARY);
        outputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        outputLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, UIStyle.Spacing.MD, 0));
        setOutputText("Most similar to \"Viva La Vida\"<br>Fingerprint: abE671deF");
        
        JPanel controlButtons = new JPanel(new FlowLayout(
            FlowLayout.CENTER,
            UIStyle.Spacing.CONTROL_BUTTONS_GAP,
            0
        ));
        controlButtons.setBackground(UIStyle.Colors.BACKGROUND_PRIMARY);
        
        playButton = new StyledButton("▶ Play");
        recordButton = new StyledButton("● Record");
        
        controlButtons.add(playButton);
        controlButtons.add(recordButton);
        
        add(outputLabel);
        add(controlButtons);
    }
    
    /**
     * Sets the output text to be displayed in the label.
     * The text is wrapped in HTML for formatting support.
     * 
     * @param text The text to display (may contain HTML formatting)
     */
    public void setOutputText(String text) {
        outputLabel.setText("<html><center>" + text + "</center></html>");
    }
    
    /**
     * Gets the output label component.
     * 
     * @return The JLabel used for displaying output text
     */
    public JLabel getOutputLabel() {
        return outputLabel;
    }
    
    /**
     * Gets the Play button.
     * 
     * @return The Play button component
     */
    public StyledButton getPlayButton() {
        return playButton;
    }
    
    /**
     * Gets the Record button.
     * 
     * @return The Record button component
     */
    public StyledButton getRecordButton() {
        return recordButton;
    }
}

