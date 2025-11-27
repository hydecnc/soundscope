package org.wavelabs.soundscope.use_case.fingerprint;

public interface FingerprintInputBoundary {
    /**
     * Executes the Fingerprint use case. After this executes, the generated fingerprint will be displayed and be ready for identification.
     */
    void execute();
}
