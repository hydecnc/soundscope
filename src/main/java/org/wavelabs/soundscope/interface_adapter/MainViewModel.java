package org.wavelabs.soundscope.interface_adapter;

import java.awt.Dimension;
import java.util.Map;

public class MainViewModel extends ViewModel<MainState> {
    public static final String TITLE = "Soundscope";
    public static final String OPEN_TEXT = "Open";
    public static final String OPEN_FILE_CHOOSER_TITLE = "Select Audio File";
    public static final String SAVE_AS_TEXT = "Save As";
    public static final String SAVE_AS_FILE_CHOOSER_TITLE = "Save Audio File";
    public static final String PLAY_TEXT = "Play";
    public static final String PAUSE_TEXT = "Pause";
    public static final String RECORD_TEXT = "Start Recording";
    public static final String STOP_RECORDING_TEXT = "Stop Recording";
    public static final String FINGERPRINT_TEXT = "Fingerprint";
    public static final String IDENTIFY_TEXT = "Identify";

    public static final Map<String, String> USE_CASE_ERROR_TITLE_MAP = Map.of(
            "file save", "Save Error",
            "process audio", "Processing Error",
            "identify", "Identify Error",
            "fingerprint", "Fingerprint Error",
            "playing", "Playback Error"
    );

    public static final String FINGERPRINT_INFO_START = "Fingerprint: ";
    public static final int FINGERPRINT_DISPLAY_LENGTH = 20;
    public static final String SONG_TITLE_INFO_START = "Song Title: ";
    public static final String ALBUM_INFO_START = "Album Title: ";

    public static final Dimension DEFAULT_BUTTON_DIMENSIONS = new Dimension(200, 200);
    public static final Dimension MIN_INFO_DIMENSIONS = new Dimension(150, 50);
    public static final Dimension MAX_INFO_PANEL_DIMENSIONS = new Dimension(1000, 100);

    public MainViewModel() {
        super("main view");
        setState(new MainState());
    }
}
