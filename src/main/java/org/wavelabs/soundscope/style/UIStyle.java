package org.wavelabs.soundscope.style;

import java.awt.*;

/**
 * Centralized UI styling constants and theme configuration.
 * All color, font, and spacing values are defined here for consistency.
 */
public class UIStyle {
    // Private constructor to prevent instantiation
    private UIStyle() {}

    // ========== Colors ==========
    public static final class Colors {
        // Background colors
        public static final Color BACKGROUND_PRIMARY = new Color(230, 242, 255); // #E6F2FF - Light blue
        public static final Color BACKGROUND_TOOLBAR = new Color(184, 212, 240); // #B8D4F0 - Medium blue
        
        // Button colors
        public static final Color BUTTON_PRIMARY = new Color(74, 144, 226);      // #4A90E2 - Blue
        public static final Color BUTTON_HOVER = new Color(160, 196, 255);      // #a0c4ff - Light blue
        public static final Color BUTTON_TEXT = Color.BLACK;
        
        // Text colors
        public static final Color TEXT_PRIMARY = new Color(51, 51, 51);          // #333333 - Dark gray
        public static final Color TEXT_SECONDARY = new Color(102, 102, 102);     // #666666 - Medium gray
        
        // Waveform colors
        public static final Color WAVEFORM_STROKE = Color.RED;
        public static final Color WAVEFORM_BACKGROUND = BACKGROUND_PRIMARY;
    }

    // ========== Fonts ==========
    public static final class Fonts {
        public static final String FAMILY_PRIMARY = "SansSerif";
        public static final int SIZE_DEFAULT = 14;
        public static final int SIZE_LARGE = 18;
        public static final int SIZE_SMALL = 12;
        
        public static final Font DEFAULT = new Font(FAMILY_PRIMARY, Font.PLAIN, SIZE_DEFAULT);
        public static final Font BOLD = new Font(FAMILY_PRIMARY, Font.BOLD, SIZE_DEFAULT);
        public static final Font LARGE = new Font(FAMILY_PRIMARY, Font.PLAIN, SIZE_LARGE);
        public static final Font SMALL = new Font(FAMILY_PRIMARY, Font.PLAIN, SIZE_SMALL);
    }

    // ========== Spacing ==========
    public static final class Spacing {
        public static final int XS = 5;
        public static final int SM = 10;
        public static final int MD = 15;
        public static final int LG = 20;
        public static final int XL = 30;
        public static final int XXL = 40;
        
        // Component-specific spacing
        public static final int TOOLBAR_PADDING = MD;
        public static final int TOOLBAR_GAP = SM;
        public static final int BOTTOM_PADDING = LG;
        public static final int BUTTON_PADDING_VERTICAL = 8;
        public static final int BUTTON_PADDING_HORIZONTAL = 16;
        public static final int CONTROL_BUTTONS_GAP = XXL;
    }

    // ========== Dimensions ==========
    public static final class Dimensions {
        public static final int WINDOW_WIDTH = 900;
        public static final int WINDOW_HEIGHT = 500;
        public static final int WAVEFORM_WIDTH = 800;
        public static final int WAVEFORM_HEIGHT = 100; // Standard height for waveform display
    }

    // ========== Borders ==========
    public static final class Borders {
        public static final int BUTTON_STROKE_WIDTH = 2;
        public static final int WAVEFORM_STROKE_WIDTH = 2;
    }

    // ========== Cursors ==========
    public static final class Cursors {
        public static final Cursor HAND = new Cursor(Cursor.HAND_CURSOR);
        public static final Cursor DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);
    }
}

