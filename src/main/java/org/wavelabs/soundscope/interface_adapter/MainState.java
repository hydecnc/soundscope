package org.wavelabs.soundscope.interface_adapter;

public class MainState {
    private boolean errorState;
    private String errorMessage;

    private String currentAudioSourcePath;
    private String songTitle;
    private String album;
    private String fingerprint;

    private boolean isRecording = false; //TODO: figure out how to push updates to this
    private boolean isPlaying = false; //TODO: figure out how to push updates to this
    private boolean playingFinished = false; //TODO: figure out how to push updates to this
    private int framesPlayed = 0; //TODO: figure out how to push updates to this

    public boolean isErrorState() {
        return errorState;
    }
    public void setErrorState(boolean errorState) {
        this.errorState = errorState;
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

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPlayingFinished() {
        return playingFinished;
    }

    public void setPlayingFinished(boolean playingFinished) {
        this.playingFinished = playingFinished;
    }

    public int getFramesPlayed() {
        return framesPlayed;
    }

    public void setFramesPlayed(int framesPlayed) {
        this.framesPlayed = framesPlayed;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
