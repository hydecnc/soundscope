package org.wavelabs.soundscope.interface_adapter;

/**
 * Utility class for formatting time values.
 *
 * <p>This class is part of the Interface Adapters layer and provides
 * standardized time formatting for the application.
 */
public final class TimeFormatter {

    /**
     * Private constructor to prevent instantiation.
     */
    private TimeFormatter() {
    }

    /**
     * Formats time in seconds to MM:SS format.
     *
     * @param seconds Time in seconds
     * @return Formatted time string
     */
    public static String formatTime(double seconds) {
        final int minutes = (int) (seconds / 60);
        final int secs = (int) (seconds % 60);
        return String.format("%d:%02d", minutes, secs);
    }
}
