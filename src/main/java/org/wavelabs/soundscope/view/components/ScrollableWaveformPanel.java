package org.wavelabs.soundscope.view.components;

import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.view.UIStyle;

import javax.swing.*;
import java.awt.*;

/**
 * Scrollable waveform panel that displays a 10-second window of audio.
 * 
 * <p>This class is part of the Frameworks & Drivers layer and provides
 * a custom JPanel for displaying audio waveforms. It supports horizontal
 * scrolling to view different portions of longer audio files and synchronizes
 * with a TimelinePanel to display time markers.
 * 
 * <p>The panel displays a 10-second window of the audio at a time and
 * automatically normalizes the waveform to fit within vertical bounds.
 */
public class ScrollableWaveformPanel extends JPanel {
    private double[] waveformData;
    private double durationSeconds = 0;
    private double viewStartTime = 0;
    private double viewDuration = 10.0;
    private JScrollBar horizontalScrollBar;
    private TimelinePanel timelinePanel;
    private Timer refreshTimer;
    private boolean isUpdatingScrollbar = false;
    
    /**
     * Constructs a ScrollableWaveformPanel with the specified timeline panel.
     * 
     * @param timelinePanel The timeline panel to synchronize with for time markers
     */
    public ScrollableWaveformPanel(TimelinePanel timelinePanel) {
        this.timelinePanel = timelinePanel;
        setBackground(UIStyle.Colors.WAVEFORM_BACKGROUND);
        setPreferredSize(new Dimension(
            UIStyle.Dimensions.WAVEFORM_WIDTH,
            UIStyle.Dimensions.WAVEFORM_HEIGHT
        ));
        setLayout(new BorderLayout());
        
        horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 100, 0, 100);
        horizontalScrollBar.addAdjustmentListener(e -> {
            if (!isUpdatingScrollbar) {
                updateViewFromScrollbar();
            }
        });
        
        generatePlaceholderWaveform();
        startRefreshTimer();
    }
    
    /**
     * Starts a refresh timer that continuously repaints the waveform panel.
     * The timer fires every 10ms to ensure smooth visual updates.
     */
    private void startRefreshTimer() {
        refreshTimer = new Timer(10, e -> repaint());
        refreshTimer.setRepeats(true);
        refreshTimer.start();
    }
    
    /**
     * Stops the refresh timer used for continuous repainting.
     */
    public void stopRefreshTimer() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
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
            
            isUpdatingScrollbar = true;
            try {
                updateScrollbarRange();
            } finally {
                isUpdatingScrollbar = false;
            }
        } else {
            this.waveformData = null;
            this.durationSeconds = 0;
        }
        
        repaint();
        if (timelinePanel != null) {
            timelinePanel.setDuration(durationSeconds);
            timelinePanel.setViewRange(viewStartTime, viewDuration);
        }
    }
    
    /**
     * Updates the horizontal scrollbar range based on the audio duration.
     * Disables scrolling if the audio fits within the visible window.
     * Preserves the current scroll position when updating the range.
     */
    private void updateScrollbarRange() {
        if (durationSeconds <= viewDuration) {
            horizontalScrollBar.setEnabled(false);
            horizontalScrollBar.setMaximum(100);
            horizontalScrollBar.setVisibleAmount(100);
            horizontalScrollBar.setValue(0);
        } else {
            horizontalScrollBar.setEnabled(true);
            int maxValue = (int) ((durationSeconds - viewDuration) * 100);
            int currentScrollValue = (int) ((viewStartTime / (durationSeconds - viewDuration)) * maxValue);
            currentScrollValue = Math.max(0, Math.min(currentScrollValue, maxValue));
            
            horizontalScrollBar.setMaximum(maxValue + 100);
            horizontalScrollBar.setVisibleAmount(100);
            horizontalScrollBar.setValue(currentScrollValue);
        }
    }
    
    /**
     * Updates the visible time range based on the scrollbar position.
     * Calculates the start time of the visible window and synchronizes with the timeline panel.
     */
    private void updateViewFromScrollbar() {
        if (durationSeconds <= viewDuration) {
            viewStartTime = 0;
        } else {
            int scrollValue = horizontalScrollBar.getValue();
            int scrollMax = horizontalScrollBar.getMaximum() - horizontalScrollBar.getVisibleAmount();
            
            if (scrollMax <= 0) {
                viewStartTime = 0;
            } else {
                double scrollRatio = (double) scrollValue / scrollMax;
                double maxStartTime = durationSeconds - viewDuration;
                viewStartTime = scrollRatio * maxStartTime;
            }
        }
        
        repaint();
        if (timelinePanel != null) {
            timelinePanel.setViewRange(viewStartTime, viewDuration);
        }
    }
    
    /**
     * Generates a placeholder waveform for display when no audio file is loaded.
     * Creates a simple sine wave pattern for visual feedback.
     */
    private void generatePlaceholderWaveform() {
        int numSamples = 200;
        waveformData = new double[numSamples];
        for (int i = 0; i < numSamples; i++) {
            waveformData[i] = Math.sin(i * 0.1) * 0.5 + Math.sin(i * 0.3) * 0.3;
        }
        durationSeconds = 10.0;
    }
    
    /**
     * Paints the waveform on the panel.
     * 
     * <p>Renders the audio waveform as a continuous blue line, normalizing
     * amplitude values to fit within the panel bounds. Only displays the
     * portion of the waveform corresponding to the current scroll position.
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
        
        double samplesPerSecond = waveformData.length / durationSeconds;
        int startSample = (int) (viewStartTime * samplesPerSecond);
        int endSample = (int) ((viewStartTime + viewDuration) * samplesPerSecond);
        endSample = Math.min(endSample, waveformData.length - 1);
        
        if (startSample >= waveformData.length || endSample < startSample) {
            return;
        }
        
        int visibleSamples = endSample - startSample;
        if (visibleSamples <= 0) {
            return;
        }
        
        double sampleWidth = (double) width / visibleSamples;
        
        double maxMagnitude = 0.0;
        for (int i = startSample; i <= endSample && i < waveformData.length; i++) {
            double amplitude = Math.max(-1.0, Math.min(1.0, waveformData[i]));
            double magnitude = Math.abs(amplitude);
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
        
        if (visibleSamples > 1) {
            java.awt.geom.GeneralPath waveformPath = new java.awt.geom.GeneralPath();
            boolean firstPoint = true;
            
            for (int i = startSample; i <= endSample && i < waveformData.length; i++) {
                int localIndex = i - startSample;
                double x = localIndex * sampleWidth;
                
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
    
    /**
     * Gets the horizontal scroll bar for navigating through the waveform.
     * 
     * @return The JScrollBar component for horizontal scrolling
     */
    public JScrollBar getHorizontalScrollBar() {
        return horizontalScrollBar;
    }
}

