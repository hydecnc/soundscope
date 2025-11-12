package org.wavelabs.soundscope.framework.ui;

import org.wavelabs.soundscope.framework.style.UIStyle;
import org.wavelabs.soundscope.framework.ui.components.StyledButton;

import javax.swing.*;
import java.awt.*;

/**
 * Bottom control panel containing output label and playback/recording controls.
 */
public class BottomControlPanel extends JPanel {
    private final JLabel outputLabel;
    private final StyledButton playButton;
    private final StyledButton recordButton;
    
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
    
    public void setOutputText(String text) {
        outputLabel.setText("<html><center>" + text + "</center></html>");
    }
    
    public JLabel getOutputLabel() {
        return outputLabel;
    }
    
    public StyledButton getPlayButton() {
        return playButton;
    }
    
    public StyledButton getRecordButton() {
        return recordButton;
    }
}

