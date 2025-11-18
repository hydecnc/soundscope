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
    private int sampleRate = 44100;
    private static final int SAMPLES_PER_PIXEL = 8; // Compression factor
    private static final int DISPLAY_INTERVAL_SECONDS = 30; // 30-second window
    
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
            this.sampleRate = audioData.getSampleRate();
            
            // Adjust panel width to accommodate waveform (for both recording and loaded files)
            // Panel width = number of 30-second intervals * width per interval
            // Account for 256x downsampling
            if (waveformData != null && waveformData.length > 0) {
                int samplesIn30Seconds = (sampleRate * DISPLAY_INTERVAL_SECONDS) / 256;
                int numberOfIntervals = (int) Math.ceil((double) waveformData.length / samplesIn30Seconds);
                int widthFor30Seconds = samplesIn30Seconds / SAMPLES_PER_PIXEL;
                int preferredWidth = numberOfIntervals * widthFor30Seconds;
                // Ensure minimum width for first window
                preferredWidth = Math.max(preferredWidth, widthFor30Seconds);
                Dimension newSize = new Dimension(preferredWidth, UIStyle.Dimensions.WAVEFORM_HEIGHT);
                setPreferredSize(newSize);
                setSize(newSize); // Also set actual size, not just preferred
                setMinimumSize(newSize);
                setMaximumSize(new Dimension(Integer.MAX_VALUE, UIStyle.Dimensions.WAVEFORM_HEIGHT));
                revalidate();
                // Notify parent scroll pane of size change
                Container parent = getParent();
                if (parent != null) {
                    parent.revalidate();
                    // If parent is a viewport, force it to update
                    if (parent instanceof JViewport) {
                        ((JViewport) parent).revalidate();
                    }
                }
            }
        } else {
            this.waveformData = null;
            this.durationSeconds = 0;
            setPreferredSize(new Dimension(
                UIStyle.Dimensions.WAVEFORM_WIDTH,
                UIStyle.Dimensions.WAVEFORM_HEIGHT
            ));
            revalidate();
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
        
        if (waveformData == null || waveformData.length == 0) {
            return;
        }
        
        // Allow drawing even if duration is 0 (during initial recording)
        if (durationSeconds <= 0 && waveformData.length > 0) {
            durationSeconds = (double) waveformData.length / sampleRate;
        }
        
        g2d.setColor(UIStyle.Colors.WAVEFORM_STROKE);
        g2d.setStroke(new BasicStroke(1.0f));
        
        int height = getHeight();
        
        // Show data progressively from left to right
        // Account for 256x downsampling: samplesIn30Seconds = (sampleRate * 30) / 256
        int samplesIn30Seconds = (sampleRate * DISPLAY_INTERVAL_SECONDS) / 256;
        int startSample = 0;
        int endSample = waveformData.length;
        
        // Always show all data continuously from the start (both during recording and after)
        // This ensures the whole waveform is visible and can be scrolled through
        startSample = 0;
        endSample = waveformData.length;
        
        int samplesToDisplay = endSample - startSample;
        if (samplesToDisplay <= 0) {
            return;
        }
        
        double verticalPadding = 10.0;
        double usableHeight = height - (verticalPadding * 2);
        
        // Calculate sample width using fixed width per sample based on 30-second window
        // This ensures consistent scaling for both recording and loaded files
        // The waveform will grow continuously and can be scrolled through
        int widthFor30Seconds = samplesIn30Seconds / SAMPLES_PER_PIXEL;
        double sampleWidth = (double) widthFor30Seconds / samplesIn30Seconds;
        
        // Find max magnitude in the samples to display
        double maxMagnitude = 0.0;
        for (int i = startSample; i < endSample; i++) {
            double magnitude = Math.abs(Math.max(-1.0, Math.min(1.0, waveformData[i])));
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
        
        if (samplesToDisplay > 1) {
            java.awt.geom.GeneralPath waveformPath = new java.awt.geom.GeneralPath();
            boolean firstPoint = true;
            
            // Draw only the actual recorded samples from left to right
            // During recording: draw all samples continuously from x=0
            // For loaded files: draw samples within the current 30-second window
            for (int i = 0; i < samplesToDisplay; i++) {
                int sampleIndex = startSample + i;
                
                // Calculate x position based on absolute sample index (continuous)
                // This ensures the waveform is displayed continuously from left to right
                double x = sampleIndex * sampleWidth;
                
                double amplitude = Math.max(-1.0, Math.min(1.0, waveformData[sampleIndex]));
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


