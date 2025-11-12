package org.wavelabs.soundscope.framework.ui;

import org.wavelabs.soundscope.framework.style.UIStyle;
import org.wavelabs.soundscope.framework.ui.components.ScrollableWaveformPanel;
import org.wavelabs.soundscope.framework.ui.components.TimelinePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window that orchestrates all UI components.
 */
public class MainWindow extends JFrame {
    private final TopToolbar topToolbar;
    private final ScrollableWaveformPanel waveformPanel;
    private final TimelinePanel timelinePanel;
    private final BottomControlPanel bottomControlPanel;
    
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
    
    private void initializeWindow() {
        setTitle("Soundscope");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UIStyle.Dimensions.WINDOW_WIDTH, UIStyle.Dimensions.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
    }
    
    public TopToolbar getTopToolbar() {
        return topToolbar;
    }
    
    public ScrollableWaveformPanel getWaveformPanel() {
        return waveformPanel;
    }
    
    public TimelinePanel getTimelinePanel() {
        return timelinePanel;
    }
    
    public BottomControlPanel getBottomControlPanel() {
        return bottomControlPanel;
    }
}

