package org.wavelabs.soundscope.use_case.fingerprint;

import org.wavelabs.soundscope.entity.Song;

public class FingerprinterOD {
    private final String fingerprint;

    public FingerprinterOD(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    @Override
    public String toString() {
        return "FingerprinterOD [fingerprint=" + fingerprint + "]";
    }

    public String getFingerprint() {
        return fingerprint;
    }
}
