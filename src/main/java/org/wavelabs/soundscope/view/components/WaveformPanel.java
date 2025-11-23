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
    private double currentPlaybackPositionSeconds = 0.0; // Current playback position in seconds
    private static final int SAMPLES_PER_PIXEL = 8; // Compression factor
    private static final int DISPLAY_INTERVAL_SECONDS = 30; // 30-second window
    
    // Cached computed values (only recalculated when audio data changes)
    private double cachedNormalizationFactor;
    private double cachedSampleWidth;
    private double cachedCenterY;
    private double cachedUsableHeight;
    private int cachedSamplesIn30Seconds;
    private boolean dataChanged = true; // Flag to indicate if data needs recalculation
    
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
        boolean audioDataChanged = false;
        
        if (audioData != null) {
            double[] newWaveformData = audioData.getAmplitudeSamples();
            double newDurationSeconds = audioData.getDurationSeconds();
            int newSampleRate = audioData.getSampleRate();
            
            // Check if audio data actually changed
            if (waveformData != newWaveformData || 
                (waveformData != null && newWaveformData != null && 
                 (waveformData.length != newWaveformData.length || 
                  durationSeconds != newDurationSeconds || 
                  sampleRate != newSampleRate))) {
                audioDataChanged = true;
            }
            
            this.waveformData = newWaveformData;
            this.durationSeconds = newDurationSeconds;
            this.sampleRate = newSampleRate;
            
            // Only update panel size when audio data actually changes
            if (audioDataChanged) {
                updatePanelSize();
            }
        } else {
            if (waveformData != null) {
                audioDataChanged = true;
            }
            this.waveformData = null;
            this.durationSeconds = 0;
            
            // Reset panel size when clearing audio data
            if (audioDataChanged) {
                setPreferredSize(new Dimension(
                    UIStyle.Dimensions.WAVEFORM_WIDTH,
                    UIStyle.Dimensions.WAVEFORM_HEIGHT
                ));
                revalidate();
            }
        }
        
        // Only recalculate waveform paths if audio data changed
        if (audioDataChanged) {
            dataChanged = true;
            recalculateWaveformPaths();
        }
        
        repaint();
    }
    
    /**
     * Updates the panel size based on the current waveform data.
     * This should only be called when audio data changes.
     */
    private void updatePanelSize() {
        if (waveformData != null && waveformData.length > 0) {
            // Adjust panel width to accommodate waveform (for both recording and loaded files)
            // Panel width = number of 30-second intervals * width per interval
            // Account for 256x downsampling
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
    }
    
    /**
     * Updates the waveform display with new audio data and playback position.
     * 
     * @param audioData The AudioData object containing amplitude samples and metadata
     * @param playbackPositionSeconds The current playback position in seconds
     */
    public void updateWaveform(AudioData audioData, double playbackPositionSeconds) {
        boolean positionChanged = this.currentPlaybackPositionSeconds != playbackPositionSeconds;
        this.currentPlaybackPositionSeconds = playbackPositionSeconds;
        
        // Only update audio data if it's different
        updateWaveform(audioData);
        
        // If only position changed and data hasn't changed, just repaint
        if (positionChanged && !dataChanged) {
            repaint();
        }
    }
    
    /**
     * Updates only the playback position without recalculating waveform paths.
     * This is more efficient when only the playback position changes.
     * 
     * @param playbackPositionSeconds The current playback position in seconds
     */
    public void updatePlaybackPosition(double playbackPositionSeconds) {
        if (this.currentPlaybackPositionSeconds != playbackPositionSeconds) {
            this.currentPlaybackPositionSeconds = playbackPositionSeconds;
            repaint();
        }
    }
    
    /**
     * Recalculates the waveform cached values.
     * This should only be called when the audio data changes.
     * Note: Waveform paths are rebuilt in paintComponent because y-coordinates depend on current panel height.
     */
    private void recalculateWaveformPaths() {
        if (waveformData == null || waveformData.length == 0) {
            cachedNormalizationFactor = 0;
            cachedSampleWidth = 0;
            dataChanged = false;
            return;
        }
        
        // Allow drawing even if duration is 0 (during initial recording)
        if (durationSeconds <= 0 && waveformData.length > 0) {
            durationSeconds = (double) waveformData.length / sampleRate;
        }
        
        int startSample = 0;
        int endSample = waveformData.length;
        int samplesToDisplay = endSample - startSample;
        
        if (samplesToDisplay <= 0) {
            cachedNormalizationFactor = 0;
            cachedSampleWidth = 0;
            dataChanged = false;
            return;
        }
        
        // Calculate and cache values that don't change with playback position or panel height
        cachedSamplesIn30Seconds = (sampleRate * DISPLAY_INTERVAL_SECONDS) / 256;
        int widthFor30Seconds = cachedSamplesIn30Seconds / SAMPLES_PER_PIXEL;
        cachedSampleWidth = (double) widthFor30Seconds / cachedSamplesIn30Seconds;
        
        // Find max magnitude in the samples to display
        double maxMagnitude = 0.0;
        for (int i = startSample; i < endSample; i++) {
            double magnitude = Math.abs(Math.max(-1.0, Math.min(1.0, waveformData[i])));
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
            }
        }
        
        if (maxMagnitude < 0.001) {
            cachedNormalizationFactor = 0;
            cachedSampleWidth = 0;
            dataChanged = false;
            return;
        }
        
        double targetMagnitude = maxMagnitude * 0.90;
        cachedNormalizationFactor = (targetMagnitude > 0.0) ? (0.90 / targetMagnitude) : 1.0;
        
        if (cachedNormalizationFactor > 10.0) {
            cachedNormalizationFactor = 10.0;
        }
        
        dataChanged = false;
    }
    
    /**
     * Paints the waveform on the panel.
     * 
     * <p>Renders the audio waveform with played and unplayed portions in different colors,
     * normalizing amplitude values to fit within the panel bounds.
     * Uses cached values for performance - only recalculates when audio data changes.
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
        
        // Recalculate if data changed (this should be rare)
        if (dataChanged) {
            recalculateWaveformPaths();
        }
        
        // If still no valid cached values, return
        if (cachedNormalizationFactor == 0 || cachedSampleWidth == 0) {
            return;
        }
        
        int height = getHeight();
        int startSample = 0;
        int endSample = waveformData.length;
        int samplesToDisplay = endSample - startSample;
        
        if (samplesToDisplay <= 0) {
            return;
        }
        
        double verticalPadding = 10.0;
        cachedUsableHeight = height - (verticalPadding * 2);
        cachedCenterY = height / 2.0;
        
        double maxNormalizedValue = 0.90;
        
        // Calculate playback position in terms of sample index (accounting for 256x downsampling)
        int playbackSampleIndex = -1;
        if (currentPlaybackPositionSeconds > 0 && durationSeconds > 0 && sampleRate > 0) {
            playbackSampleIndex = (int) (currentPlaybackPositionSeconds * sampleRate / 256);
        }
        
        if (samplesToDisplay > 1) {
            // Build paths using cached normalization factor and sample width
            java.awt.geom.GeneralPath playedPath = new java.awt.geom.GeneralPath();
            java.awt.geom.GeneralPath unplayedPath = new java.awt.geom.GeneralPath();
            boolean playedFirstPoint = true;
            boolean unplayedFirstPoint = true;
            
            for (int i = 0; i < samplesToDisplay; i++) {
                int sampleIndex = startSample + i;
                double x = sampleIndex * cachedSampleWidth;
                
                double amplitude = Math.max(-1.0, Math.min(1.0, waveformData[sampleIndex]));
                double normalizedAmplitude = amplitude * cachedNormalizationFactor;
                normalizedAmplitude = Math.max(-maxNormalizedValue, Math.min(maxNormalizedValue, normalizedAmplitude));
                
                double y = cachedCenterY - (normalizedAmplitude * cachedUsableHeight / 2.0);
                
                boolean isPlayed = playbackSampleIndex >= 0 && sampleIndex <= playbackSampleIndex;
                
                if (isPlayed) {
                    if (playedFirstPoint) {
                        playedPath.moveTo(x, y);
                        playedFirstPoint = false;
                    } else {
                        playedPath.lineTo(x, y);
                    }
                } else {
                    if (unplayedFirstPoint) {
                        unplayedPath.moveTo(x, y);
                        unplayedFirstPoint = false;
                    } else {
                        unplayedPath.lineTo(x, y);
                    }
                }
            }
            
            if (!unplayedFirstPoint) {
                g2d.setColor(UIStyle.Colors.WAVEFORM_STROKE);
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.draw(unplayedPath);
            }
            
            if (!playedFirstPoint) {
                g2d.setColor(UIStyle.Colors.WAVEFORM_PLAYED);
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.draw(playedPath);
            }
            
            // Draw red vertical line for playback position indicator
            if (playbackSampleIndex >= 0) {
                double x = playbackSampleIndex * cachedSampleWidth;
                if (x >= 0 && x <= getWidth()) {
                    g2d.setColor(UIStyle.Colors.PLAYBACK_INDICATOR);
                    g2d.setStroke(new BasicStroke(2.0f));
                    g2d.drawLine((int) x, 0, (int) x, getHeight());
                }
            }
        }
    }
}


