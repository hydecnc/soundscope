package org.wavelabs.soundscope.view.components;

import org.wavelabs.soundscope.view.UIStyle;

import javax.swing.*;
import java.awt.*;

/**
 * Timeline panel that displays time intervals below the waveform.
 * 
 * <p>This class is part of the Frameworks & Drivers layer and provides
 * a visual timeline showing time markers (in MM:SS format) synchronized
 * with the waveform display. It displays markers at 10-second intervals
 * for the currently visible portion of the audio.
 */
public class TimelinePanel extends JPanel {
    private double durationSeconds = 0;
    private double viewStartTime = 0;
    private double viewDuration = 2.0;
    
    /**
     * Constructs a TimelinePanel with default settings.
     */
    public TimelinePanel() {
        setBackground(UIStyle.Colors.BACKGROUND_PRIMARY);
        setPreferredSize(new Dimension(UIStyle.Dimensions.WAVEFORM_WIDTH, 30));
        setMinimumSize(new Dimension(UIStyle.Dimensions.WAVEFORM_WIDTH, 30));
    }
    
    /**
     * Sets the total duration of the audio file.
     * 
     * @param durationSeconds The total duration in seconds
     */
    public void setDuration(double durationSeconds) {
        this.durationSeconds = durationSeconds;
        repaint();
    }
    
    /**
     * Sets the visible time range for the timeline.
     * 
     * @param startTime The start time of the visible range in seconds
     * @param duration The duration of the visible range in seconds
     */
    public void setViewRange(double startTime, double duration) {
        this.viewStartTime = Math.max(0, Math.min(startTime, durationSeconds - duration));
        this.viewDuration = duration;
        repaint();
    }
    
    /**
     * Gets the start time of the currently visible range.
     * 
     * @return The start time in seconds
     */
    public double getViewStartTime() {
        return viewStartTime;
    }
    
    /**
     * Gets the duration of the currently visible range.
     * 
     * @return The duration in seconds
     */
    public double getViewDuration() {
        return viewDuration;
    }
    
    /**
     * Paints the timeline with time markers.
     * 
     * <p>Draws a horizontal line and time markers at 10-second intervals
     * for the currently visible portion of the audio. Time labels are
     * displayed in MM:SS format below each marker.
     * 
     * @param g The Graphics context for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (durationSeconds <= 0) {
            return;
        }
        
        int width = getWidth();
        
        g2d.setColor(UIStyle.Colors.TEXT_PRIMARY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(0, 0, width, 0);
        
        double endTime = Math.min(viewStartTime + viewDuration, durationSeconds);
        
        g2d.setColor(UIStyle.Colors.TEXT_PRIMARY);
        g2d.setFont(UIStyle.Fonts.SMALL);
        
        double firstMark = Math.floor(viewStartTime / 10.0) * 10.0;
        
        for (double time = firstMark; time <= endTime; time += 10.0) {
            if (time < 0) continue;
            
            double relativeTime = time - viewStartTime;
            int x = (int) ((relativeTime / viewDuration) * width);
            
            if (x >= 0 && x <= width) {
                g2d.drawLine(x, 0, x, 8);
                
                String timeLabel = formatTime(time);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(timeLabel);
                int labelX = x - labelWidth / 2;
                
                labelX = Math.max(2, Math.min(labelX, width - labelWidth - 2));
                
                g2d.drawString(timeLabel, labelX, 20);
            }
        }
    }
    
    /**
     * Formats a time value in seconds to MM:SS format.
     * 
     * @param seconds The time in seconds
     * @return Formatted time string in MM:SS format
     */
    private String formatTime(double seconds) {
        int minutes = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%d:%02d", minutes, secs);
    }
}

