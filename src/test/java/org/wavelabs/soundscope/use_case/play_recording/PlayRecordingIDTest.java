package org.wavelabs.soundscope.use_case.play_recording;

import org.junit.Assert;
import org.junit.Test;

public class PlayRecordingIDTest {

    @Test
    public void ConstructorValidInputGivesInstance() {
        PlayRecordingID id = new PlayRecordingID("path/to/file.wav", true);
        Assert.assertEquals("path/to/file.wav", id.getSourcePath());
        Assert.assertTrue(id.shouldRestartFromBeginning());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ConstructorNullPathThrowsException() {
        new PlayRecordingID(null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ConstructorBlankPathThrowsException() {
        new PlayRecordingID("   ", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ConstructorEmptyPathThrowsException() {
        new PlayRecordingID("", false);
    }
}
