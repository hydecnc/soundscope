package org.wavelabs.soundscope.framework.ui.components;

import org.wavelabs.soundscope.framework.style.UIStyle;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Reusable styled button component with consistent styling and hover effects.
 */
public class StyledButton extends JButton {
    
    public StyledButton(String text) {
        super(text);
        applyDefaultStyle();
        addHoverEffect();
    }
    
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

