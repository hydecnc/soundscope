package org.wavelabs.soundscope.use_cases.saveRecording;

/**
 * Input Data for save recording use case
 * includes the file path
 */
public class SaveRecordingID {
    private final String filePath;

    public SaveRecordingID(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() { return filePath; }
}
