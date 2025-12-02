package org.wavelabs.soundscope.use_case.fingerprint;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Assert;
import org.junit.Test;

public class FingerprinterTest {
    @Test
    public void correctFingerprintGeneration()
        throws UnsupportedAudioFileException, IOException, URISyntaxException {
        // Read in the file
        URL url = getClass().getResource("/fingerprint_test.wav");
        Assert.assertNotNull(url);
        File file = new File(url.toURI());
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
        final byte[] bytes = inputStream.readAllBytes();

        AudioFormat audioFormat = inputStream.getFormat();
        Fingerprinter fingerprinter =
            new Fingerprinter((int) audioFormat.getSampleRate(), audioFormat.getChannels());
        fingerprinter.start();
        fingerprinter.processChunk(bytes, bytes.length);
        fingerprinter.stop();
        final String result = fingerprinter.getFingerprint();
        fingerprinter.close();

        System.out.println(result);
        final String expectedResult =
            "AQACDKEiNZQITfmRA89xPajyC00oWcGZ5zh-NPURymHx6Md_TG_RK2AsDWGaEgcb5sI5NMmN2nLwNCpyPpCTp8iHKaqOHr8iHE-ZiLh8lFHaFe6RI9mzCz-OJzni37C-QA9_HM-hJX4RPsvRxyn8FP2IZ9lxHuclOBkjLcUJHfjxPInxi3hZNHn1occZ4capCzt64gnMZ8hJuLyQtDnxER55kNpb_MfWHc-PkwqO5j2eJcKJU8jnBM_SQdBPfElIIZSyXYQrpsJ0gcePKcsR3oGmL6hZ_Phs_EIrXcjTZQeeF3cG_4Kt4TryjMcP3_gO_fiF8PiFG9fxPkb9Cj6u9TBfJK6L_MR_NMefoPyJ5sCPrzg-nBgaPciJa1kgvkKuE66Y4PkEOsL7wjn2ODj8ozojHK8QX5dxNhX6HEdaHVeWHD-DrsTkUS-8aSgTIVcgsZ2SIcwxXcV34TRuiXhWLujhf7B-4eGRU3CVxygPjemNTya0Bz90xPSD92ieJD_647j-4CF-uBZzPInwBIHOH88V5MM9HvWVDk1E5AtxHA9G70Ej_Sh7NJ6OfkEyX4MfPPvAHlfQx8L0PfCJH2HO4ZC5G7oe9LkR5pkRZkSPK1FyNJNqo_8EXcL4g0244kuO7StOPDk0sYL25PiP84OVcNCTUWiOOomOS-sQ5sgzCx6-H1M66IqOH4_wQw-LnQiz41RsNNJzlEtYNM9wqqi5PgjzjJDDIz_6Fu5R7TleFv-Rujm-RMMv7Do6PUQz0kKfCHZq6Jk15ILHgH6GE1cuvMb2Cz_SL0Of4T_07NDzwH1nhB7C7LgY1KOFZn-hj8N44toJmpkxZQt6pTg0MT2eHNoTH-eHRlpQdqHQC1pa4foRitzhZ4d_lBemVDnK4UN2PA7u7AGP48aMfSu-Y9oP0duKnsiV473Q72jyBdeH6eHQPAiP5lHw4tHxH430EGWPxtPRL0cyv_Cz4xkN9riCx8L3YDvxI8w5HDJ3Q9eDPkeYZx7S8TguSUfT2yj5CbqE8QebcEW55JjyFSeeHJpYQXty_CrOw0o46Mko-MKjI-c6NCfyXPDwHVO6Q1d0_HhEHHpY7Gh2IqZyNHzUocsStmg-XEXN9UGYZzzkEPnRw31R7anwsviP1A2-RMOPXUenB83IRugToXEYQ3uuIRc8BvSH8_gu3Nh-4Uf6Zegz_NCjHKrzwOFvhN4RZsfDoI2ii2h2POIEhT-usaCZY8oWoU9xaGJ6fMmhPT7OD420QGMXCr3QtMKjH-F3-NmRjkfRMMox_dC149eh_zgimkETneg_oZGyRUXZo6dMNM9wUol0pCeeBb9woaldhG-iYv-RJ4cqxkqQJt8VXOCP6Qz-iLiY4QN85IROXDiNRzh-HDp0CC9e6PBh6DjU49Dh4xB_4IZ4AKIYVVAIQIBQTBoAABGACQYIQMIAxhAQlBHigFCMUggEYkAILkASACFGGKFAQGQBI8AJAAgjBoBBnRAGAUEMABQAxhBhAjBGHBFGEs0UAEQRJwCAQChBnEDMKEQMQZIAQQkAAggGEENGEWYUMVAjSBkhoChEhCLKODANYAIAB5hZQBADvBDCKkIwMAYxIA01TEDlBgMAACNIIIAICCBTADAChiCEQkYEIIAoQAxDigDuiGBCCSWANUIABRgxhCkoDBOAAKQkAsoQAJyQDjjkJAAAMAAgYU4BoABgwDCFDBDIKIIMEwgMIQQQhiEDnCEAAUKMUxIIQIBQhAgggNECMQCAJwkBTYAwQCCiADIAEaGYAUYAAghQgBCGqAPcEcGEEsoBQ40AQAFmCFNQGCYIQAZZAxQxUgAnpAPIKQUEBQAQJgZQADDDHDJACGIYIchYgcAQQkDDDBDEKUEYIoQopxgggACBCDECCAOBAU4AoIBxShgCABCAECeCUUAoYBxijjDkEFOIOWQZWhIxJJRx8CsrjBUA";

        assert (result.equals(expectedResult));
    }
}
