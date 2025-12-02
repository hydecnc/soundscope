package org.wavelabs.soundscope.data_access;

import org.junit.Test;
import org.wavelabs.soundscope.entity.Song;

/**
 * Tests AcousticID Fingerprint Lookups
 */
public class AcoustIDFingerprintLookupTest {
    public static final String fingerprint = "AQABz0qUkZK4oOfhL-CPc4e5C_wW2H2QH9uDL4cvoT8UNQ-eHtsE8cceeFJx-LiiHT-aPzhxoc-Opj_eI5d2hOFyMJRzfDk-QSsu7fBxqZDMHcfxPfDIoPWxv9C1o3yg44d_3Df2GJaUQeeR-cb2HfaPNsdxHj2PJnpwPMN3aPcEMzd-_MeB_Ej4D_CLP8ghHjkJv_jh_UDuQ8xnILwunPg6hF2R8HgzvLhxHVYP_ziJX0eKPnIE1UePMByDJyg7wz_6yELsB8n4oDmDa0Gv40hf6D3CE3_wH6HFaxCPUD9-hNeF5MfWEP3SCGym4-SxnXiGs0mRjEXD6fgl4LmKWrSChzzC33ge9PB3otyJMk-IVC6R8MTNwD9qKQ_CC8kPv4THzEGZS8GPI3x0iGVUxC1hRSizC5VzoamYDi-uR7iKPhGSI82PkiWeB_eHijvsaIWfBCWH5AjjCfVxZ1TQ3CvCTclGnEMfHbnZFA8pjD6KXwd__Cn-Y8e_I9cq6CR-4S9KLXqQcsxxoWh3eMxiHI6TIzyPv0M43YHz4yte-Cv-4D16Hv9F9C9SPUdyGtZRHV-OHEeeGD--BKcjVLOK_NCDXMfx44dzHEiOZ0Z44Rf6DH5R3uiPj4d_PKolJNyRJzyu4_CTD2WOvzjKH9GPb4cUP1Av9EuQd8fGCFee4JlRHi18xQh96NLxkCgfWFKOH6WGeoe4I3za4c5hTscTPEZTES1x8kE-9MQPjT8a8gh5fPgQZtqCFj9MDvp6fDx6NCd07bjx7MLR9AhtnFnQ70GjOcV0opmm4zpY3SOa7HiwdTtyHa6NC4e-HN-OfC5-OP_gLe2QDxfUCz_0w9l65HiPAz9-IaGOUA7-4MZ5CWFOlIfe4yUa6AiZGxf6w0fFxsjTOdC6Itbh4mGD63iPH9-RFy909XAMj7mC5_BvlDyO6kGTZKJxHUd4NDwuZUffw_5RMsde5CWkJAgXnDReNEaP6DTOQ65yaD88HoeX8fge-DSeHo9Qa8cTHc80I-_RoHxx_UHeBxrJw62Q34Kd7MEfpCcu6BLeB1ePw6OO4sOF_sHhmB504WWDZiEu8sKPpkcfCT9xfej0o0lr4T5yNJeOvjmu40w-TDmqHXmYgfFhFy_M7tD1o0cO_B2ms2j-ACEEQgQgAIwzTgAGmBIKIImNQAABwgQATAlhDGCCEIGIIM4BaBgwQBogEBIOESEIA8ARI5xAhxEFmAGAMCKAURKQQpQzRAAkCCBQEAKkQYIYIQQxCixCDADCABMAE0gpJIgyxhEDiCKCCIGAEIgJIQByAhFgGACCACMRQEyBAoxQiHiCBCFOECQFAIgAABR2QAgFjCDMA0AUMIoAIMChQghChASGEGeYEAIAIhgBSErnJPPEGWYAMgw05AhiiGHiBBBGGSCQcQgwRYJwhDDhgCSCSSEIQYwILoyAjAIigBFEUQK8gAYAQ5BCAAjkjCCAEEMZAUQAZQCjCCkpCgFMCCiIcVIAZZgilAQAiSHQECOcQAQIc4QClAHAjDDGkAGAMUoBgyhihgEChFCAAWEIEYwIJYwViAAlHCBIGEIEAEIQAoBwwgwiEBAEEEOoEwBY4wRwxAhBgAcKAESIQAwwIowRFhoBhAE";
    public static final int testDuration = 641;

    /**
     * Tests .getClosestMatchID
     */
    @Test
    public void matchIDTest(){
        AcoustIDIdentify identifier = AcoustIDIdentify.getAcoustIDIdentify();

        String result = identifier.getClosestMatchID(fingerprint, testDuration);
        String expectedResult = "9ff43b6a-4f16-427c-93c2-92307ca505e0";

        assert(result.equals(expectedResult));
    }

    /**
     * Tests that requests are spaced apart by the minimum amount of time.
     */
    @Test
    public void APIQuerySpacingTest(){
        AcoustIDIdentify fingerprinter = AcoustIDIdentify.getAcoustIDIdentify();

        long start = System.currentTimeMillis();
        fingerprinter.getClosestMatchID(fingerprint, testDuration);
        fingerprinter.getClosestMatchID(fingerprint, testDuration);
        long end = System.currentTimeMillis();

        assert(end - start > AcoustIDApiConstants.REQUEST_SPACING_MILLIS);
    }

    /**
     * Tests .getClosestMatchMetadata
     */
    @Test
    public void matchWithMetadataTest(){
        AcoustIDIdentify fingerprinter = AcoustIDIdentify.getAcoustIDIdentify();

        Song.SongMetadata resultMetadata = fingerprinter.getClosestMatchMetadata(fingerprint, testDuration);

        String[] expectedArtists = {"M83"};
        Song.SongMetadata expectedMetadata = new Song.SongMetadata(
                "Lower Your Eyelids to Die With the Sun",
                "cd2e7c47-16f5-46c6-a37c-a1eb7bf599ff",
                "9ff43b6a-4f16-427c-93c2-92307ca505e0",
                "Before the Dawn Heals Us",
                expectedArtists
        );

        // NOTE: We do not just use ".equals" as this would compare the artist arrays by reference, not value
        assert(resultMetadata.title().equals(expectedMetadata.title()));
        assert(resultMetadata.album().equals(expectedMetadata.album()));
        assert(resultMetadata.musicBrainzID().equals(expectedMetadata.musicBrainzID()));
        assert(resultMetadata.acoustIDTrackID().equals(expectedMetadata.acoustIDTrackID()));
        assert(resultMetadata.artists().length==expectedArtists.length);
        for(int i=0;i<expectedArtists.length;i++){
            assert(resultMetadata.artists()[i].equals(expectedArtists[i]));
        }
    }
}
