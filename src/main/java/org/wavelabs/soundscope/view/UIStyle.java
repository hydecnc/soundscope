package org.wavelabs.soundscope.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

/**
 * Centralized UI styling constants and theme configuration.
 * 
 * <p>This class is part of the Frameworks & Drivers layer and provides
 * a centralized location for all UI styling constants including colors,
 * fonts, spacing, dimensions, borders, and cursors. All UI components
 * should reference these constants to ensure visual consistency.
 * 
 * <p>This class cannot be instantiated and contains only static nested
 * classes with public static final fields.
 */
public final class UIStyle {
    /**
     * Private constructor to prevent instantiation.
     */
    private UIStyle() {
    }

    public static final class Colors {
        public static final Color BACKGROUND_PRIMARY = new Color(230, 242, 255);
        public static final Color BACKGROUND_TOOLBAR = new Color(184, 212, 240);
        
        public static final Color BUTTON_PRIMARY = new Color(74, 144, 226);
        public static final Color BUTTON_HOVER = new Color(160, 196, 255);
        public static final Color BUTTON_TEXT = Color.BLACK;
        
        public static final Color TEXT_PRIMARY = new Color(51, 51, 51);
        public static final Color TEXT_SECONDARY = new Color(102, 102, 102);
        
        public static final Color WAVEFORM_STROKE = new Color(0, 100, 255);
        // Red for played portion
        public static final Color WAVEFORM_PLAYED = Color.RED;
        // Orange for clipped/overload portions
        public static final Color WAVEFORM_CLIPPED = new Color(255, 165, 0);
        public static final Color WAVEFORM_BACKGROUND = Color.WHITE;
        public static final Color PLAYBACK_INDICATOR = Color.RED;
    }

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

    public static final class Spacing {
        public static final int XS = 5;
        public static final int SM = 10;
        public static final int MD = 15;
        public static final int LG = 20;
        public static final int XL = 30;
        public static final int XXL = 40;
        
        public static final int TOOLBAR_PADDING = MD;
        public static final int TOOLBAR_GAP = SM;
        public static final int BOTTOM_PADDING = LG;
        public static final int BUTTON_PADDING_VERTICAL = 8;
        public static final int BUTTON_PADDING_HORIZONTAL = 16;
        public static final int CONTROL_BUTTONS_GAP = XXL;
    }

    public static final class Dimensions {
        public static final int WINDOW_WIDTH = 900;
        public static final int WINDOW_HEIGHT = 500;
        public static final int WAVEFORM_WIDTH = 800;
        public static final int WAVEFORM_HEIGHT = 100;
    }

    public static final class Borders {
        public static final int BUTTON_STROKE_WIDTH = 2;
        public static final int WAVEFORM_STROKE_WIDTH = 2;
    }

    public static final class Cursors {
        public static final Cursor HAND = new Cursor(Cursor.HAND_CURSOR);
        public static final Cursor DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);
    }
}
