package org.wavelabs.soundscope.view.components;

import org.wavelabs.soundscope.entity.AudioData;
import org.wavelabs.soundscope.view.UIStyle;

import javax.swing.*;
import java.awt.*;

/**
 * High-performance waveform panel that displays audio waveform.
 * * <p>This class is part of the Frameworks & Drivers layer and provides
 * a custom JPanel for displaying audio waveforms with optimized rendering.
 * Uses cached envelope arrays and background computation for smooth performance.
 * * <p><strong>Rendering Strategy:</strong> Uses a modified Linear/Power scale 
 * rather than Logarithmic (dB). This preserves visual silence for low-volume 
 * sections while maintaining dynamic range for high-volume sections.
 */
public class WaveformPanel extends JPanel {
    // Core Data
    private double[] waveformData;
    private double durationSeconds = 0;
    private int sampleRate = 44100;
    private volatile double currentPlaybackPositionSeconds = 0.0;
    
    // Rendering Configuration
    private static final int SAMPLES_PER_PIXEL = 8;
    private static final int DISPLAY_INTERVAL_SECONDS = 30;
    private static final long MIN_REPAINT_INTERVAL_MS = 16; // ~60fps cap
    
    // Visual Tuning
    // Lower values boost small volumes more. Higher values compress high volumes more.
    // 0.5 = square root (boosts small volumes significantly)
    // 0.7 = moderate boost for small volumes while still showing high volumes
    private static final double AMPLITUDE_POWER_SCALE = 0.7; 
    private static final double VERTICAL_PADDING_PERCENT = 0.95; // Use 95% of available height

    // Caching for Performance
    private double cachedSampleWidth;
    private int cachedSamplesIn30Seconds;
    private boolean dataChanged = true;
    
    private float[] cachedMaxPerPixel;
    private float[] cachedMinPerPixel;
    private int cachedEnvelopeWidth = -1;
    private int cachedEnvelopeHeight = -1;
    private volatile boolean envelopeComputationInProgress = false;
    
    private long lastRepaintTime = 0;
    
    /**
     * Constructs a WaveformPanel with default settings.
     */
    public WaveformPanel() {
        setBackground(UIStyle.Colors.WAVEFORM_BACKGROUND);
        setPreferredSize(new Dimension(
            UIStyle.Dimensions.WAVEFORM_WIDTH,
            UIStyle.Dimensions.WAVEFORM_HEIGHT
        ));
        
        setDoubleBuffered(true);
        setOpaque(true); // Performance hint
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (waveformData != null && waveformData.length > 0) {
                    invalidateEnvelopeCache();
                    computeEnvelopeAsync();
                }
            }
        });
    }
    
    /**
     * Updates the waveform display with new audio data.
     * * @param audioData The AudioData object containing amplitude samples and metadata
     */
    public void updateWaveform(AudioData audioData) {
        boolean audioDataChanged = false;
        
        if (audioData != null) {
            double[] newWaveformData = audioData.getAmplitudeSamples();
            double newDurationSeconds = audioData.getDurationSeconds();
            int newSampleRate = audioData.getSampleRate();
            
            // Check for reference equality first, then structural changes
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
            
            if (audioDataChanged) {
                updatePanelSize();
            }
        } else {
            if (waveformData != null) {
                audioDataChanged = true;
            }
            this.waveformData = null;
            this.durationSeconds = 0;
            
            if (audioDataChanged) {
                setPreferredSize(new Dimension(
                    UIStyle.Dimensions.WAVEFORM_WIDTH,
                    UIStyle.Dimensions.WAVEFORM_HEIGHT
                ));
                revalidate();
            }
        }
        
        if (audioDataChanged) {
            dataChanged = true;
            invalidateEnvelopeCache();
            // Compute immediately if small enough, otherwise async
            if (waveformData != null && waveformData.length > 0) {
                 if (getWidth() > 0) computeEnvelope(); 
            }
            computeEnvelopeAsync();
        }
        
        repaintThrottled();
    }
    
    private void updatePanelSize() {
        if (waveformData != null && waveformData.length > 0) {
            int samplesIn30Seconds = (sampleRate * DISPLAY_INTERVAL_SECONDS) / 256;
            // Prevent division by zero
            if (samplesIn30Seconds == 0) samplesIn30Seconds = 1;
            int numberOfIntervals = (int) Math.ceil((double) waveformData.length / samplesIn30Seconds);
            int widthFor30Seconds = samplesIn30Seconds / SAMPLES_PER_PIXEL;
            int preferredWidth = numberOfIntervals * widthFor30Seconds;
            
            preferredWidth = Math.max(preferredWidth, widthFor30Seconds);
            
            Dimension newSize = new Dimension(preferredWidth, UIStyle.Dimensions.WAVEFORM_HEIGHT);
            setPreferredSize(newSize);
            setSize(newSize);
            setMinimumSize(newSize);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, UIStyle.Dimensions.WAVEFORM_HEIGHT));
            
            revalidate();
            Container parent = getParent();
            if (parent != null) {
                parent.revalidate();
                if (parent instanceof JViewport) {
                    ((JViewport) parent).revalidate();
                }
            }
        }
    }
    
    /**
     * Updates the waveform display with new audio data and playback position.
     */
    public void updateWaveform(AudioData audioData, double playbackPositionSeconds) {
        boolean positionChanged = Math.abs(this.currentPlaybackPositionSeconds - playbackPositionSeconds) > 0.001;
        this.currentPlaybackPositionSeconds = playbackPositionSeconds;
        
        updateWaveform(audioData);
        
        if (positionChanged && !dataChanged) {
            repaintThrottled();
        }
    }
    
    /**
     * Updates only the playback position without recalculating waveform paths.
     */
    public void updatePlaybackPosition(double playbackPositionSeconds) {
        if (Math.abs(this.currentPlaybackPositionSeconds - playbackPositionSeconds) > 0.001) {
            this.currentPlaybackPositionSeconds = playbackPositionSeconds;
            repaintThrottled();
        }
    }
    
    /**
     * Scrolls the parent viewport to show the latest part of the waveform.
     * This is useful during recording to auto-scroll to the end.
     * 
     * @param audioData The current audio data to determine scroll position
     */
    public void scrollToLatest(AudioData audioData) {
        if (audioData == null || waveformData == null || waveformData.length == 0) {
            return;
        }
        
        Container parent = getParent();
        if (parent instanceof JViewport) {
            JViewport viewport = (JViewport) parent;
            int panelWidth = getWidth();
            int viewportWidth = viewport.getWidth();
            
            if (panelWidth <= viewportWidth) {
                // Content fits in viewport, no need to scroll
                return;
            }
            
            // Calculate the position based on the current duration
            // Use the same logic as updatePanelSize to determine pixel position
            double currentDuration = audioData.getDurationSeconds();
            int samplesIn30Seconds = (sampleRate * DISPLAY_INTERVAL_SECONDS) / 256;
            if (samplesIn30Seconds == 0) samplesIn30Seconds = 1;
            
            int widthFor30Seconds = samplesIn30Seconds / SAMPLES_PER_PIXEL;
            double pixelsPerSecond = (double) widthFor30Seconds / DISPLAY_INTERVAL_SECONDS;
            int currentPixelPosition = (int) (currentDuration * pixelsPerSecond);
            
            // Scroll to show the latest part with some padding
            int scrollX = Math.min(Math.max(0, currentPixelPosition - viewportWidth + 50), panelWidth - viewportWidth);
            
            // Scroll smoothly to the latest position
            final int finalScrollX = scrollX;
            SwingUtilities.invokeLater(() -> {
                viewport.setViewPosition(new Point(finalScrollX, 0));
            });
        }
    }
    
    private void repaintThrottled() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRepaintTime >= MIN_REPAINT_INTERVAL_MS) {
            lastRepaintTime = currentTime;
            repaint();
        }
    }
    
    private void invalidateEnvelopeCache() {
        cachedMaxPerPixel = null;
        cachedMinPerPixel = null;
        cachedEnvelopeWidth = -1;
        cachedEnvelopeHeight = -1;
    }
    
    private void computeEnvelopeAsync() {
        if (envelopeComputationInProgress || waveformData == null || waveformData.length == 0) {
            return;
        }
        
        int w = getWidth();
        int h = getHeight();
        
        if (w <= 0 || h <= 0) return;
        
        if (w == cachedEnvelopeWidth && h == cachedEnvelopeHeight && 
            cachedMaxPerPixel != null && cachedMinPerPixel != null) {
            return;
        }
        
        envelopeComputationInProgress = true;
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                computeEnvelope();
                return null;
            }
            
            @Override
            protected void done() {
                envelopeComputationInProgress = false;
                dataChanged = false;
                if (SwingUtilities.isEventDispatchThread()) {
                    repaintThrottled();
                } else {
                    SwingUtilities.invokeLater(WaveformPanel.this::repaintThrottled);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Calculates the Min/Max amplitude for every pixel column.
     * This is the heavy lifting of waveform visualization.
     */
    private void computeEnvelope() {
        if (waveformData == null || waveformData.length == 0) {
            invalidateEnvelopeCache();
            return;
        }
        
        int w = getWidth();
        int h = getHeight();
        
        if (w <= 0 || h <= 0) return;
        
        // Return early if cache is already valid for this size
        if (w == cachedEnvelopeWidth && h == cachedEnvelopeHeight && 
            cachedMaxPerPixel != null && cachedMinPerPixel != null) {
            return;
        }
        
        int totalPixels = (int) Math.ceil(waveformData.length / (double) SAMPLES_PER_PIXEL);
        int pixelsToCompute = Math.min(totalPixels, w);
        
        if (pixelsToCompute <= 0) {
            invalidateEnvelopeCache();
            return;
        }
        
        // Allocate only what is needed, or reuse if possible (GC optimization could be done here, 
        // but re-allocating on resize is generally acceptable)
        float[] newMaxCache = new float[pixelsToCompute];
        float[] newMinCache = new float[pixelsToCompute];
        
        int block = SAMPLES_PER_PIXEL;
        
        for (int px = 0; px < pixelsToCompute; px++) {
            int iStart = px * block;
            int iEnd = Math.min(iStart + block, waveformData.length);
            
            if (iStart >= waveformData.length) {
                newMaxCache[px] = 0.0f;
                newMinCache[px] = 0.0f;
                continue;
            }
            
            double localMax = -1.0; // Amplitudes are -1.0 to 1.0
            double localMin = 1.0;
            
            // Inner loop: find peak within the sample block representing this pixel
            for (int i = iStart; i < iEnd; i++) {
                double v = waveformData[i];
                if (v > localMax) localMax = v;
                if (v < localMin) localMin = v;
            }
            
            // Safety clamp
            if (localMax < -1.0) localMax = -1.0;
            if (localMax > 1.0) localMax = 1.0;
            if (localMin < -1.0) localMin = -1.0;
            if (localMin > 1.0) localMin = 1.0;
            
            newMaxCache[px] = (float) localMax;
            newMinCache[px] = (float) localMin;
        }
        
        // Atomic update of cache references
        cachedMaxPerPixel = newMaxCache;
        cachedMinPerPixel = newMinCache;
        cachedEnvelopeWidth = w;
        cachedEnvelopeHeight = h;
        
        // Update scaling factors used for playback cursor
        cachedSamplesIn30Seconds = (sampleRate * DISPLAY_INTERVAL_SECONDS) / 256;
        if (cachedSamplesIn30Seconds == 0) cachedSamplesIn30Seconds = 1;
        
        int widthFor30Seconds = cachedSamplesIn30Seconds / SAMPLES_PER_PIXEL;
        cachedSampleWidth = (double) widthFor30Seconds / cachedSamplesIn30Seconds;
    }
    
    /**
     * Converts a raw amplitude (-1.0 to 1.0) to a vertical pixel offset from center.
     * Uses a Power scale to boost small volumes while compressing high volumes.
     */
    private double mapAmplitude(double amp, int height) {
        // 1. Absolute Value
        double absAmp = Math.abs(amp);
        
        // 2. Clamp
        absAmp = Math.max(0.0, Math.min(1.0, absAmp));
        
        // 3. Power Curve (The "Compression" Logic)
        // Power < 1.0 boosts small volumes (0.1^0.7 ≈ 0.20, so 2x boost)
        // Power = 1.0 is Linear (no compression)
        // Power > 1.0 compresses small volumes (not what we want)
        // Using 0.7 gives good visibility to small volumes while still showing high volumes
        double normalized = Math.pow(absAmp, AMPLITUDE_POWER_SCALE);
        
        // 4. Scale to pixels and ensure it never exceeds half the height
        double halfHeight = height / 2.0;
        double usableHeight = halfHeight * VERTICAL_PADDING_PERCENT;
        double offset = normalized * usableHeight;
        
        // 5. Clamp to ensure we never exceed the usable height
        return Math.min(offset, usableHeight);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (waveformData == null || waveformData.length == 0) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        // Optimize rendering quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        // 1. Draw Background
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        int width = getWidth();
        int height = getHeight();
        
        // 2. Check Cache Validity
        if (dataChanged || width != cachedEnvelopeWidth || height != cachedEnvelopeHeight) {
            if (cachedMaxPerPixel == null || cachedEnvelopeWidth != width) {
                // If critical mismatch, compute strictly now (avoids empty flash)
                 if (width > 0) computeEnvelope();
            } else if (!envelopeComputationInProgress) {
                // Otherwise update in background
                computeEnvelopeAsync();
            }
        }
        
        if (cachedMaxPerPixel == null || cachedMinPerPixel == null) {
            return;
        }
        
        double halfY = height / 2.0;
        int pixelsToDraw = Math.min(cachedMaxPerPixel.length, width);
        
        // 3. Calculate Playback Cursor X
        double playbackPos = currentPlaybackPositionSeconds;
        int playbackX = -1;
        if (playbackPos > 0 && durationSeconds > 0 && sampleRate > 0 && cachedSampleWidth > 0) {
            // Note: 256 divisor comes from previous logic, likely related to how audio is chunked in your system.
            // Ensure this constant matches your AudioData chunking logic.
            int playbackSampleIndex = (int) (playbackPos * sampleRate / 256);
            playbackX = (int) (playbackSampleIndex * cachedSampleWidth);
        }
        
        Color unplayedColor = UIStyle.Colors.WAVEFORM_STROKE;
        Color playedColor = UIStyle.Colors.WAVEFORM_PLAYED;
        
        // 4. Draw Waveform Lines
        // Using a loop of drawLine is surprisingly fast in Java2D for < 2000 lines.
        // For 4k+ width, a GeneralPath might be slightly faster, but drawLine is robust.
        
        g2d.setColor(unplayedColor);
        Color currentColor = unplayedColor;
        
        for (int px = 0; px < pixelsToDraw; px++) {
            float minVal = cachedMinPerPixel[px];
            float maxVal = cachedMaxPerPixel[px];
            
            double yMaxOffset = mapAmplitude(maxVal, height);
            double yMinOffset = mapAmplitude(minVal, height); // mapAmplitude handles abs internally
            
            // Calculate screen coordinates
            int yTop = (int) (halfY - yMaxOffset);
            int yBottom = (int) (halfY + yMinOffset);
            
            // Clip to panel bounds to prevent drawing outside
            yTop = Math.max(0, Math.min(height - 1, yTop));
            yBottom = Math.max(0, Math.min(height - 1, yBottom));
            
            // Ensure visual visibility: if sound exists but pixel height is 0, force 1px
            if (yTop == yBottom && (maxVal > 0.001 || minVal < -0.001)) {
                if (yBottom < height - 1) {
                    yBottom++; 
                } else if (yTop > 0) {
                    yTop--;
                }
            }
            
            // Determine Color (Played vs Unplayed)
            Color targetColor = (playbackX >= 0 && px <= playbackX) ? playedColor : unplayedColor;
            
            // State change minimization
            if (currentColor != targetColor) {
                g2d.setColor(targetColor);
                currentColor = targetColor;
            }
            
            g2d.drawLine(px, yTop, px, yBottom);
        }
        
        // 5. Draw Playback Head Line
        if (playbackX >= 0 && playbackX < width) {
            g2d.setColor(UIStyle.Colors.PLAYBACK_INDICATOR);
            g2d.setStroke(new BasicStroke(1.5f)); // Slightly thinner for precision
            g2d.drawLine(playbackX, 0, playbackX, height);
        }
    }
}
