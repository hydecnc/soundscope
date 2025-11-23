package org.wavelabs.soundscope.use_case.fingerprint;

public interface FingerprintLookupDataAccessInterface {
    public String getClosestMatchID(String fingerprint);
}