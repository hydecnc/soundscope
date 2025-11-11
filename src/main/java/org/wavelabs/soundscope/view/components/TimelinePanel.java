package org.wavelabs.soundscope.view.components;

import org.wavelabs.soundscope.style.UIStyle;

import javax.swing.*;
import java.awt.*;

/**
 * Timeline panel that displays time intervals below the waveform.
 * Shows 2-second intervals.
 */
public class TimelinePanel extends JPanel {
    private double durationSeconds = 0;
    private double viewStartTime = 0; // Start time of visible portion (in seconds)
    private double viewDuration = 2.0; // Duration of visible portion (2 seconds)
    
    public TimelinePanel() {
        setBackground(UIStyle.Colors.BACKGROUND_PRIMARY);
        setPreferredSize(new Dimension(UIStyle.Dimensions.WAVEFORM_WIDTH, 30));
        setMinimumSize(new Dimension(UIStyle.Dimensions.WAVEFORM_WIDTH, 30));
    }
    
    /**
     * Sets the total duration of the audio file.
     */
    public void setDuration(double durationSeconds) {
        this.durationSeconds = durationSeconds;
        repaint();
    }
    
    /**
     * Sets the visible time range (for scrolling).
     */
    public void setViewRange(double startTime, double duration) {
        this.viewStartTime = Math.max(0, Math.min(startTime, durationSeconds - duration));
        this.viewDuration = duration;
        repaint();
    }
    
    /**
     * Gets the current view start time.
     */
    public double getViewStartTime() {
        return viewStartTime;
    }
    
    /**
     * Gets the current view duration.
     */
    public double getViewDuration() {
        return viewDuration;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (durationSeconds <= 0) {
            return;
        }
        
        int width = getWidth();
        
        // Draw timeline line
        g2d.setColor(UIStyle.Colors.TEXT_PRIMARY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(0, 0, width, 0);
        
        // Calculate time range visible
        double endTime = Math.min(viewStartTime + viewDuration, durationSeconds);
        
        // Draw 10-second interval markers
        g2d.setColor(UIStyle.Colors.TEXT_PRIMARY);
        g2d.setFont(UIStyle.Fonts.SMALL);
        
        // Find first 10-second mark within or before visible range
        double firstMark = Math.floor(viewStartTime / 10.0) * 10.0;
        
        // Draw marks every 10 seconds
        for (double time = firstMark; time <= endTime; time += 10.0) {
            if (time < 0) continue;
            
            // Calculate x position
            double relativeTime = time - viewStartTime;
            int x = (int) ((relativeTime / viewDuration) * width);
            
            if (x >= 0 && x <= width) {
                // Draw tick mark
                g2d.drawLine(x, 0, x, 8);
                
                // Draw time label
                String timeLabel = formatTime(time);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(timeLabel);
                int labelX = x - labelWidth / 2;
                
                // Keep label within bounds
                labelX = Math.max(2, Math.min(labelX, width - labelWidth - 2));
                
                g2d.drawString(timeLabel, labelX, 20);
            }
        }
    }
    
    /**
     * Formats time in seconds to MM:SS format.
     */
    private String formatTime(double seconds) {
        int minutes = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%d:%02d", minutes, secs);
    }
}

