package org.wavelabs.soundscope.use_cases.fingerprint;

public interface FingerprintLookupDataAccessInterface {
    public String getClosestMatch(String fingerprint);
}