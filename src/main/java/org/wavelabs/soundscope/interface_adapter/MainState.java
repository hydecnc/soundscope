package org.wavelabs.soundscope.interface_adapter;

public class MainState {
    private String currentAudioSourcePath;

    private String errorMessage;

    private boolean successfulSave = false;

    //TODO: add data that needs to be stored for the main state
    public boolean isSuccessfulSave() {
        return successfulSave;
    }

    public void setSuccessfulSave(boolean successfulSave) {
        this.successfulSave = successfulSave;
    }

    public String getCurrentAudioSourcePath() {
        return currentAudioSourcePath;
    }

    public void setCurrentAudioSourcePath(String currentAudioSourcePath) {
        this.currentAudioSourcePath = currentAudioSourcePath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
