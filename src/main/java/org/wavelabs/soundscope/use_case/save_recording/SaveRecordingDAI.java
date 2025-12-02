package org.wavelabs.soundscope.use_case.save_recording;

import java.io.IOException;

/**
 * Data Access Interface (DAI) for the save-recording use case.
 *
 * <p>This interface defines the gateway through which the save-recording
 * interactor accesses and updates the audio data to be saved, as well as the
 * mechanism responsible for writing that data to persistent storage.</p>
 *
 * <p>Implementations of this interface may store the in-memory recording,
 * interact with the filesystem, or provide mock objects for testing. By
 * depending only on this abstraction, the interactor remains independent of
 * concrete storage technologies and external frameworks.</p>
 */
public interface SaveRecordingDAI {
    /**
     * Returns whether the file save was successful.
     *
     * <p>This method eventually invokes the implementation of FileSaver, passing down its argument.</p>
     *
     * @return the result of file save; never {@code null}
     */
    boolean saveToFile(String filePath) throws IOException;
  
    /**
     * Returns whether there is an audio loaded in the entity.
     *
     * 
     * @return AudioRecording != null;
     */
    boolean hasAudioRecording();
}
