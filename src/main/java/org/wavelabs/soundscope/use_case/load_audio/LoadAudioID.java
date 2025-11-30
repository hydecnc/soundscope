package org.wavelabs.soundscope.use_case.load_audio;

import java.io.File;

public class LoadAudioID {
    private final File file;

    public LoadAudioID(File file) {this.file = file;}

    public File getFile() {return file;}
}
