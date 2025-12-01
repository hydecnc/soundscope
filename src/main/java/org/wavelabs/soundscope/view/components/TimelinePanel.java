package org.wavelabs.soundscope.view.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import org.wavelabs.soundscope.interface_adapter.TimeFormatter;
import org.wavelabs.soundscope.view.UIStyle;

/**
 * Timeline panel that displays time markers synchronized with the waveform.
 *
 * <p>This class is part of the Frameworks & Drivers layer and provides
 * a timeline display showing markers every 15 seconds.
 */
public class TimelinePanel extends JPanel {
    // Show marker every 15 seconds
    private static final int MARKER_INTERVAL_SECONDS = 15;
    // Must match WaveformPanel
    private static final int SAMPLES_PER_PIXEL = 8;
    private double durationSeconds;
    private int sampleRate = 44100;

    /**
     * Constructs a TimelinePanel with default settings.
     */
    public TimelinePanel() {
        setBackground(UIStyle.Colors.WAVEFORM_BACKGROUND);
        setPreferredSize(new Dimension(
            UIStyle.Dimensions.WAVEFORM_WIDTH,
                // Timeline height
                30
        ));
    }

    /**
     * Updates the timeline with new audio data.
     *
     * @param durationSeconds The duration of the audio in seconds
     * @param sampleRate      The sample rate of the audio
     */
    public void updateTimeline(double durationSeconds, int sampleRate) {
        this.durationSeconds = durationSeconds;
        this.sampleRate = sampleRate;

        if (durationSeconds > 0 && sampleRate > 0) {
            // Calculate panel width based on duration
            // Account for 256x downsampling
            final int samplesIn30Seconds = (sampleRate * 30) / 256;
            final int widthFor30Seconds = samplesIn30Seconds / SAMPLES_PER_PIXEL;
            final int totalIntervals = (int) Math.ceil(durationSeconds / 30.0);
            final int preferredWidth = totalIntervals * widthFor30Seconds;
            setPreferredSize(new Dimension(preferredWidth, 30));
            revalidate();
        }

        repaint();
    }

    /**
     * Paints the timeline with time markers.
     *
     * @param g The Graphics context for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (durationSeconds <= 0 || sampleRate <= 0) {
            return;
        }

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        final FontMetrics fm = g2d.getFontMetrics();

        // Calculate pixels per second
        // Account for 256x downsampling: samples are downsampled by 256
        // Total pixels = (total samples / 256) / SAMPLES_PER_PIXEL
        // Total seconds = durationSeconds
        // Pixels per second = total pixels / total seconds
        final int totalSamples = (int) (sampleRate * durationSeconds);
        final int downsampledSamples = totalSamples / 256;
        final int totalPixels = downsampledSamples / SAMPLES_PER_PIXEL;
        final double pixelsPerSecond = totalPixels / durationSeconds;

        // Draw markers every 15 seconds
        int currentTime = 0;
        while (currentTime <= durationSeconds) {
            final double x = currentTime * pixelsPerSecond;

            // Draw vertical line
            g2d.drawLine((int) x, 0, (int) x, getHeight());

            // Draw time label
            final String timeLabel = TimeFormatter.formatTime(currentTime);
            final int labelWidth = fm.stringWidth(timeLabel);
            g2d.drawString(timeLabel, (int) x - labelWidth / 2, getHeight() - 5);

            currentTime += MARKER_INTERVAL_SECONDS;
        }
    }
}

