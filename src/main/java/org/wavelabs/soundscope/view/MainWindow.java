package org.wavelabs.soundscope.view;
import org.wavelabs.soundscope.view.components.ScrollableWaveformPanel;
import org.wavelabs.soundscope.view.components.TimelinePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window that orchestrates all UI components.
 * 
 * <p>This class is part of the Frameworks & Drivers layer and serves as the
 * main container for all UI components. It arranges the toolbar, waveform panel,
 * timeline, and control panel in a BorderLayout.
 */
public class MainWindow extends JFrame {
    private final TopToolbar topToolbar;
    private final ScrollableWaveformPanel waveformPanel;
    private final TimelinePanel timelinePanel;
    private final BottomControlPanel bottomControlPanel;
    
    /**
     * Constructs the main application window and initializes all UI components.
     */
    public MainWindow() {
        initializeWindow();
        
        topToolbar = new TopToolbar();
        timelinePanel = new TimelinePanel();
        waveformPanel = new ScrollableWaveformPanel(timelinePanel);
        bottomControlPanel = new BottomControlPanel();
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(UIStyle.Colors.BACKGROUND_PRIMARY);
        
        JPanel waveformContainer = new JPanel(new BorderLayout());
        waveformContainer.setBackground(UIStyle.Colors.BACKGROUND_PRIMARY);
        waveformContainer.add(waveformPanel, BorderLayout.CENTER);
        waveformContainer.add(waveformPanel.getHorizontalScrollBar(), BorderLayout.SOUTH);
        waveformContainer.add(timelinePanel, BorderLayout.NORTH);
        
        contentPane.add(topToolbar, BorderLayout.NORTH);
        contentPane.add(waveformContainer, BorderLayout.CENTER);
        contentPane.add(bottomControlPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Initializes the window properties including title, size, and close operation.
     */
    private void initializeWindow() {
        setTitle("Soundscope");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UIStyle.Dimensions.WINDOW_WIDTH, UIStyle.Dimensions.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
    }
    
    /**
     * Gets the top toolbar component.
     * 
     * @return The TopToolbar instance
     */
    public TopToolbar getTopToolbar() {
        return topToolbar;
    }
    
    /**
     * Gets the scrollable waveform panel component.
     * 
     * @return The ScrollableWaveformPanel instance
     */
    public ScrollableWaveformPanel getWaveformPanel() {
        return waveformPanel;
    }
    
    /**
     * Gets the timeline panel component.
     * 
     * @return The TimelinePanel instance
     */
    public TimelinePanel getTimelinePanel() {
        return timelinePanel;
    }
    
    /**
     * Gets the bottom control panel component.
     * 
     * @return The BottomControlPanel instance
     */
    public BottomControlPanel getBottomControlPanel() {
        return bottomControlPanel;
    }
}

