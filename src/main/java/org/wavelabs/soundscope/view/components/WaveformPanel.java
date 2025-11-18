package org.wavelabs.soundscope.view.components;

import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.view.UIStyle;

import javax.swing.*;
import java.awt.*;

/**
 * Simple waveform panel that displays audio waveform.
 * 
 * <p>This class is part of the Frameworks & Drivers layer and provides
 * a custom JPanel for displaying audio waveforms.
 */
public class WaveformPanel extends JPanel {
    private double[] waveformData;
    private double durationSeconds = 0;
    
    /**
     * Constructs a WaveformPanel with default settings.
     */
    public WaveformPanel() {
        setBackground(UIStyle.Colors.WAVEFORM_BACKGROUND);
        setPreferredSize(new Dimension(
            UIStyle.Dimensions.WAVEFORM_WIDTH,
            UIStyle.Dimensions.WAVEFORM_HEIGHT
        ));
    }
    
    /**
     * Updates the waveform display with new audio data.
     * 
     * @param audioData The AudioData object containing amplitude samples and metadata
     */
    public void updateWaveform(AudioData audioData) {
        if (audioData != null) {
            this.waveformData = audioData.getAmplitudeSamples();
            this.durationSeconds = audioData.getDurationSeconds();
        } else {
            this.waveformData = null;
            this.durationSeconds = 0;
        }
        
        repaint();
    }
    
    /**
     * Paints the waveform on the panel.
     * 
     * <p>Renders the audio waveform as a continuous blue line, normalizing
     * amplitude values to fit within the panel bounds.
     * 
     * @param g The Graphics context for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        if (waveformData == null || waveformData.length == 0 || durationSeconds <= 0) {
            return;
        }
        
        g2d.setColor(UIStyle.Colors.WAVEFORM_STROKE);
        g2d.setStroke(new BasicStroke(1.0f));
        
        int width = getWidth();
        int height = getHeight();
        
        double verticalPadding = 10.0;
        double usableHeight = height - (verticalPadding * 2);
        
        double sampleWidth = (double) width / waveformData.length;
        
        double maxMagnitude = 0.0;
        for (double amplitude : waveformData) {
            double magnitude = Math.abs(Math.max(-1.0, Math.min(1.0, amplitude)));
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
            }
        }
        
        if (maxMagnitude < 0.001) {
            return;
        }
        
        double targetMagnitude = maxMagnitude * 0.90;
        double normalizationFactor = (targetMagnitude > 0.0) ? (0.90 / targetMagnitude) : 1.0;
        
        if (normalizationFactor > 10.0) {
            normalizationFactor = 10.0;
        }
        
        double maxNormalizedValue = 0.90;
        double centerY = height / 2.0;
        
        if (waveformData.length > 1) {
            java.awt.geom.GeneralPath waveformPath = new java.awt.geom.GeneralPath();
            boolean firstPoint = true;
            
            for (int i = 0; i < waveformData.length; i++) {
                double x = i * sampleWidth;
                
                double amplitude = Math.max(-1.0, Math.min(1.0, waveformData[i]));
                double normalizedAmplitude = amplitude * normalizationFactor;
                normalizedAmplitude = Math.max(-maxNormalizedValue, Math.min(maxNormalizedValue, normalizedAmplitude));
                
                double y = centerY - (normalizedAmplitude * usableHeight / 2.0);
                
                if (firstPoint) {
                    waveformPath.moveTo(x, y);
                    firstPoint = false;
                } else {
                    waveformPath.lineTo(x, y);
                }
            }
            
            g2d.draw(waveformPath);
        }
    }
}


