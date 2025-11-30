package org.wavelabs.soundscope.interface_adapter.play_recording;

import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingIB;
import org.wavelabs.soundscope.use_case.play_recording.PlayRecordingID;

public class PlayRecordingController {
    private final PlayRecordingIB playRecordingInteractor;

    public PlayRecordingController(PlayRecordingIB playRecordingInteractor) {
        this.playRecordingInteractor = playRecordingInteractor;
    }

    public void stop(){
        playRecordingInteractor.stop();
    }

    public void pause(){
        playRecordingInteractor.pause();
    }

    public void play(String currentAudioSourcePath, boolean restartFromBeginning){
        playRecordingInteractor.play(new PlayRecordingID(currentAudioSourcePath, restartFromBeginning));
    }
}
