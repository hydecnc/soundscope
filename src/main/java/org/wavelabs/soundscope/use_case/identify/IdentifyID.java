package org.wavelabs.soundscope.use_case.identify;

/**
 * Input data structure for the Identify use case.
 *
 * <p>Represents the data passed from the Controller to the Use Case Interactor.
 * This is a simple data transfer object containing the fingerprint that has been generated for the file.
 *
 * <p>This class is part of the Application Business Rules layer and follows
 * Clean Architecture principles by using only standard Java types and domain entities.
 */
public class IdentifyID {
    private final String fingerprint;

    /**
     * Constructs an IdentifyID with the specified string.
     *
     * @param fingerprint the provided fingerprint
     */
    public IdentifyID(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    /**
     * Retrieves the fingerprint provided.
     *
     * @return fingerprint
     */
    public String getFingerprint() {
        return fingerprint;
    }
}
