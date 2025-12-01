package org.wavelabs.soundscope.use_case.identify;

import org.junit.Before;
import org.junit.Test;
import org.wavelabs.soundscope.entity.Song;

/**
 * Tests for the IdentifyInteractor.
 *
 * <p>This test suite provides 100% code coverage for IdentifyInteractor,
 * testing all code paths including:
 * <ul>
 *   <li>Constructor with valid dependencies</li>
 *   <li>identify() with valid song data</li>
 *   <li>identify() with a DAI exception (error handling)</li>
 * </ul>
 */
public class IdentifyInteractorTest {
    private static final String TEST_FINGEPRRINT = "test fingeprint";
    private static final int TEST_DURATION = 73;
    private static final String[] TEST_ARTISTS = {"artist 1", "artist 2"};
    private static final Song.SongMetadata TEST_CORRECT_METADATA = new Song.SongMetadata(
        "test title",
        "test music brainz id",
        "test acoustID track ID",
        "test album",
        TEST_ARTISTS
    );
    private static final String ERROR_MESSAGE = "fingerprint not found";

    private MockIdentifyPresenter mockIdentifyPresenter;
    private Song songData;
    private IdentifyInteractor identifyInteractor;

    @Before
    public void setup(){
        IdentifyDAI mockIdentifyDAI = new MockIdentifyDAI();
        mockIdentifyPresenter = new MockIdentifyPresenter();
        songData = new Song();
        identifyInteractor = new IdentifyInteractor(songData, mockIdentifyPresenter, mockIdentifyDAI);
    }

    @Test
    public void testValidSong(){
        songData.setDuration(TEST_DURATION);
        songData.setFingerprint(TEST_FINGEPRRINT);

        identifyInteractor.identify();

        IdentifyOD expectedOutput = new IdentifyOD(
                TEST_CORRECT_METADATA.title(),
                TEST_CORRECT_METADATA.artists(),
                TEST_CORRECT_METADATA.album()
        );

        assert(!mockIdentifyPresenter.isErrorState());
        assert(mockIdentifyPresenter.getOutputData().equals(expectedOutput));
        assert(mockIdentifyPresenter.getErrorMessage() == null);
    }

    @Test
    public void testLookupError(){
        songData.setDuration(0);
        songData.setFingerprint("");

        identifyInteractor.identify();

        String expectedErrorMessage = new IdentifyDAI.FingerprintMatchNotFoundException(ERROR_MESSAGE).getMessage();

        assert(mockIdentifyPresenter.isErrorState());
        assert(mockIdentifyPresenter.getErrorMessage().equals(expectedErrorMessage));
        assert(mockIdentifyPresenter.getOutputData() == null);
    }

    private static class MockIdentifyPresenter implements IdentifyOB {
        private boolean errorState;
        private IdentifyOD outputData;
        private String errorMessage;

        @Override
        public void updateSongAttributes(IdentifyOD outputData) {
            errorState = false;
            this.outputData = outputData;
            this.errorMessage = null;
        }

        @Override
        public void presentError(String errorMessage) {
            errorState = true;
            this.outputData = null;
            this.errorMessage = errorMessage;
        }

        public boolean isErrorState() {
            return errorState;
        }

        public IdentifyOD getOutputData() {
            return outputData;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    private static class MockIdentifyDAI implements IdentifyDAI {
        /**
         * Not needed for the testing, so just returns an empty string
         * @param fingerprint hash code to fingerprint
         * @param duration    the duration of the audio
         * @return an empty string
         */
        @Override
        public String getClosestMatchID(String fingerprint, int duration) {
            return "";
        }

        /**
         * Returns test metadata if fingerprint and duration do not match test quantities, throws an error otherwise
         * @param fingerprint hash code to fingerprint
         * @param duration    duration of audio
         * @return
         */
        @Override
        public Song.SongMetadata getClosestMatchMetadata(String fingerprint, int duration) {
            if(fingerprint.equals(TEST_FINGEPRRINT) && duration == TEST_DURATION){
                return TEST_CORRECT_METADATA;
            }else{
                throw new FingerprintMatchNotFoundException(ERROR_MESSAGE);
            }
        }
    }
}