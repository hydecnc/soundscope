package org.wavelabs.soundscope.framework.ui.components;

import org.wavelabs.soundscope.domain.AudioData;
import org.wavelabs.soundscope.framework.style.UIStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.geom.Line2D;

/**
 * Scrollable waveform panel that displays a 10-second window of audio.
 * Supports horizontal scrolling to view different portions of the waveform.
 */
public class ScrollableWaveformPanel extends JPanel {
    private double[] waveformData;
    private double durationSeconds = 0;
    private double viewStartTime = 0;
    private double viewDuration = 10.0;
    private JScrollBar horizontalScrollBar;
    private TimelinePanel timelinePanel;
    private Timer refreshTimer;
    
    public ScrollableWaveformPanel(TimelinePanel timelinePanel) {
        this.timelinePanel = timelinePanel;
        setBackground(UIStyle.Colors.WAVEFORM_BACKGROUND);
        setPreferredSize(new Dimension(
            UIStyle.Dimensions.WAVEFORM_WIDTH,
            UIStyle.Dimensions.WAVEFORM_HEIGHT
        ));
        setLayout(new BorderLayout());
        
        horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 100, 0, 100);
        horizontalScrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                updateViewFromScrollbar();
            }
        });
        
        generatePlaceholderWaveform();
        startRefreshTimer();
    }
    
    private void startRefreshTimer() {
        refreshTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        refreshTimer.setRepeats(true);
        refreshTimer.start();
    }
    
    public void stopRefreshTimer() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }
    
    public void updateWaveform(AudioData audioData) {
        if (audioData != null) {
            this.waveformData = audioData.getAmplitudeSamples();
            this.durationSeconds = audioData.getDurationSeconds();
            updateScrollbarRange();
            viewStartTime = 0;
            horizontalScrollBar.setValue(0);
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
    
    private void updateScrollbarRange() {
        if (durationSeconds <= viewDuration) {
            horizontalScrollBar.setEnabled(false);
            horizontalScrollBar.setMaximum(100);
            horizontalScrollBar.setVisibleAmount(100);
        } else {
            horizontalScrollBar.setEnabled(true);
            int maxValue = (int) ((durationSeconds - viewDuration) * 100);
            horizontalScrollBar.setMaximum(maxValue + 100);
            horizontalScrollBar.setVisibleAmount(100);
        }
    }
    
    private void updateViewFromScrollbar() {
        if (durationSeconds <= viewDuration) {
            viewStartTime = 0;
        } else {
            int scrollValue = horizontalScrollBar.getValue();
            int scrollMax = horizontalScrollBar.getMaximum() - horizontalScrollBar.getVisibleAmount();
            double scrollRatio = scrollMax > 0 ? (double) scrollValue / scrollMax : 0;
            viewStartTime = scrollRatio * (durationSeconds - viewDuration);
        }
        
        repaint();
        if (timelinePanel != null) {
            timelinePanel.setViewRange(viewStartTime, viewDuration);
        }
    }
    
    private void generatePlaceholderWaveform() {
        int numSamples = 200;
        waveformData = new double[numSamples];
        for (int i = 0; i < numSamples; i++) {
            waveformData[i] = Math.sin(i * 0.1) * 0.5 + Math.sin(i * 0.3) * 0.3;
        }
        durationSeconds = 10.0;
    }
    
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
        g2d.setStroke(new BasicStroke(UIStyle.Borders.WAVEFORM_STROKE_WIDTH));
        
        int width = getWidth();
        int height = getHeight();
        
        double verticalPadding = 10.0;
        double usableHeight = height - verticalPadding;
        double bottomY = height - verticalPadding;
        
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
        
        for (int i = startSample; i < endSample && i < waveformData.length - 1; i++) {
            int localIndex = i - startSample;
            double x1 = localIndex * sampleWidth;
            
            double amplitude1 = Math.max(-1.0, Math.min(1.0, waveformData[i]));
            double magnitude1 = Math.abs(amplitude1);
            double normalizedMagnitude1 = magnitude1 * normalizationFactor;
            normalizedMagnitude1 = Math.min(normalizedMagnitude1, maxNormalizedValue);
            
            double y1 = bottomY - (normalizedMagnitude1 * usableHeight);
            
            double x2 = (localIndex + 1) * sampleWidth;
            double amplitude2 = Math.max(-1.0, Math.min(1.0, waveformData[i + 1]));
            double magnitude2 = Math.abs(amplitude2);
            double normalizedMagnitude2 = magnitude2 * normalizationFactor;
            normalizedMagnitude2 = Math.min(normalizedMagnitude2, maxNormalizedValue);
            
            double y2 = bottomY - (normalizedMagnitude2 * usableHeight);
            
            double topBoundary = verticalPadding + (usableHeight * 0.10);
            y1 = Math.max(topBoundary, Math.min(bottomY, y1));
            y2 = Math.max(topBoundary, Math.min(bottomY, y2));
            
            g2d.draw(new Line2D.Double(x1, y1, x2, y2));
        }
    }
    
    public JScrollBar getHorizontalScrollBar() {
        return horizontalScrollBar;
    }
}

