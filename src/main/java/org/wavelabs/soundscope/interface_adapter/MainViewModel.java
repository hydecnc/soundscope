package org.wavelabs.soundscope.interface_adapter;

import java.awt.*;
import java.util.Map;

public class MainViewModel extends ViewModel<MainState>{
    public final static String TITLE = "Soundscope";
    public final static String OPEN_TEXT = "Open";
    public final static String OPEN_FILE_CHOOSER_TITLE = "Select Audio File";
    public final static String SAVE_AS_TEXT = "Save As";
    public final static String SAVE_AS_FILE_CHOOSER_TITLE = "Save Audio File";
    public final static String PLAY_TEXT = "Play";
    public final static String PAUSE_TEXT = "Pause";
    public final static String RECORD_TEXT = "Start Recording";
    public final static String STOP_RECORDING_TEXT = "Stop Recording";
    public final static String FINGERPRINT_TEXT = "Fingerprint";
    public final static String IDENTIFY_TEXT = "Identify";
    public final static Map<String, String> USE_CASE_ERROR_TITLE_MAP = Map.of(
            "file save", "Save Error",
            "process audio", "Processing Error",
            "identify", "Identify Error",
            "fingerprint", "Fingerprint Error",
            "playing", "Playback Error"
    );

    public final static Dimension DEFAULT_BUTTON_DIMENSIONS = new Dimension(200, 200);
    

    public MainViewModel() {
        super("main view");
        setState(new MainState());
    }
}
