package org.wavelabs.soundscope.use_case.identify;

/**
 * Input boundary for the Identify use case
 */
public interface IdentifyIB {
    /**
     * Performs the Identify use case
     * @return IdentifyOutputData
     */
    void identify(int duration);
}
