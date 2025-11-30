package org.wavelabs.soundscope.use_case.load_audio;

public interface LoadAudioOB {
    void presentSuccess(LoadAudioOD outputData);
    void presentFailure(String errorMessage);
}
