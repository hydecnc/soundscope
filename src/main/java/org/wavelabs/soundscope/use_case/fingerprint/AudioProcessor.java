package org.wavelabs.soundscope.use_cases.fingerprint;


public interface AudioProcessor {
    void start();

    void processChunk(byte[] chunk, int numBytes);

    void stop();
}
