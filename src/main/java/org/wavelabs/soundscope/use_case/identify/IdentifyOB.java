package org.wavelabs.soundscope.use_case.identify;

public interface IdentifyOB {
    void updateSongAttributes(IdentifyOD outputData);

    void presentError(String errorMessage);

}
