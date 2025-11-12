package org.wavelabs.soundscope.framework.ui.components;

import org.wavelabs.soundscope.framework.style.UIStyle;

import javax.swing.*;
import java.awt.*;

/**
 * Timeline panel that displays time intervals below the waveform.
 */
public class TimelinePanel extends JPanel {
    private double durationSeconds = 0;
    private double viewStartTime = 0;
    private double viewDuration = 2.0;
    
    public TimelinePanel() {
        setBackground(UIStyle.Colors.BACKGROUND_PRIMARY);
        setPreferredSize(new Dimension(UIStyle.Dimensions.WAVEFORM_WIDTH, 30));
        setMinimumSize(new Dimension(UIStyle.Dimensions.WAVEFORM_WIDTH, 30));
    }
    
    public void setDuration(double durationSeconds) {
        this.durationSeconds = durationSeconds;
        repaint();
    }
    
    public void setViewRange(double startTime, double duration) {
        this.viewStartTime = Math.max(0, Math.min(startTime, durationSeconds - duration));
        this.viewDuration = duration;
        repaint();
    }
    
    public double getViewStartTime() {
        return viewStartTime;
    }
    
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
    
    private String formatTime(double seconds) {
        int minutes = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%d:%02d", minutes, secs);
    }
}

