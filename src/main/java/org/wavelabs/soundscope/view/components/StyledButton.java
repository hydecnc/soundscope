package org.wavelabs.soundscope.view.components;

import org.wavelabs.soundscope.view.UIStyle;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Reusable styled button component with consistent styling and hover effects.
 * 
 * <p>This class is part of the Frameworks & Drivers layer and extends JButton
 * to provide a consistent button style throughout the application. It applies
 * default styling from UIStyle and adds hover effects for better user experience.
 */
public class StyledButton extends JButton {
    
    /**
     * Constructs a StyledButton with the specified text.
     * 
     * @param text The text to display on the button
     */
    public StyledButton(String text) {
        super(text);
        applyDefaultStyle();
        addHoverEffect();
    }
    
    /**
     * Applies the default styling to the button.
     * Sets colors, font, border, and cursor according to UIStyle constants.
     */
    private void applyDefaultStyle() {
        setBackground(UIStyle.Colors.BUTTON_PRIMARY);
        setForeground(UIStyle.Colors.BUTTON_TEXT);
        setFont(UIStyle.Fonts.DEFAULT);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(
                UIStyle.Spacing.BUTTON_PADDING_VERTICAL,
                UIStyle.Spacing.BUTTON_PADDING_HORIZONTAL,
                UIStyle.Spacing.BUTTON_PADDING_VERTICAL,
                UIStyle.Spacing.BUTTON_PADDING_HORIZONTAL
            )
        ));
        setFocusPainted(false);
        setCursor(UIStyle.Cursors.HAND);
    }
    
    /**
     * Adds hover effect listeners to change button color on mouse enter/exit.
     * Changes background color to hover color when mouse enters, restores on exit.
     */
    private void addHoverEffect() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(UIStyle.Colors.BUTTON_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(UIStyle.Colors.BUTTON_PRIMARY);
            }
        });
    }
}

