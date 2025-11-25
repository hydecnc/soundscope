package org.wavelabs.soundscope.use_case.fingerprint;

/**
 * The output boundary for the Fingerprinter use case.
 */
public interface FingerprinterOB {
    /**
     * Prepares the success view for the Fingerprinter use case.
     * 
     * @param outputData the output data
     */
    void prepareSuccessView(FingerprinterOD outputData);

    /**
     * Prepares the failure view for the Login Use Case.
     * 
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
