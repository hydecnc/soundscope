package org.wavelabs.soundscope.view;
import org.wavelabs.soundscope.view.components.StyledButton;

import javax.swing.*;
import java.awt.*;

/**
 * Top toolbar component containing file and action buttons.
 * 
 * <p>This class is part of the Frameworks & Drivers layer and provides
 * a horizontal toolbar with buttons for file operations (Open, Save As)
 * and audio processing actions (Fingerprint, Identify).
 */
public class TopToolbar extends JPanel {
    private final StyledButton openButton;
    private final StyledButton saveButton;
    private final StyledButton fingerprintButton;
    private final StyledButton identifyButton;
    
    /**
     * Constructs a TopToolbar with all action buttons initialized.
     */
    public TopToolbar() {
        setLayout(new FlowLayout(FlowLayout.LEFT, UIStyle.Spacing.TOOLBAR_GAP, UIStyle.Spacing.TOOLBAR_PADDING));
        setBackground(UIStyle.Colors.BACKGROUND_TOOLBAR);
        setBorder(BorderFactory.createEmptyBorder(
            UIStyle.Spacing.TOOLBAR_PADDING,
            UIStyle.Spacing.TOOLBAR_PADDING,
            UIStyle.Spacing.TOOLBAR_PADDING,
            UIStyle.Spacing.TOOLBAR_PADDING
        ));
        
        openButton = new StyledButton("Open");
        saveButton = new StyledButton("Save As");
        fingerprintButton = new StyledButton("Fingerprint");
        identifyButton = new StyledButton("Identify");
        
        add(openButton);
        add(saveButton);
        add(fingerprintButton);
        add(identifyButton);
    }
    
    /**
     * Gets the Open button.
     * 
     * @return The Open button component
     */
    public StyledButton getOpenButton() {
        return openButton;
    }
    
    /**
     * Gets the Save As button.
     * 
     * @return The Save As button component
     */
    public StyledButton getSaveButton() {
        return saveButton;
    }
    
    /**
     * Gets the Fingerprint button.
     * 
     * @return The Fingerprint button component
     */
    public StyledButton getFingerprintButton() {
        return fingerprintButton;
    }
    
    /**
     * Gets the Identify button.
     * 
     * @return The Identify button component
     */
    public StyledButton getIdentifyButton() {
        return identifyButton;
    }
}

