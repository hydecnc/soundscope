package org.wavelabs.soundscope.use_case.fingerprint;


public interface AudioProcessor {
    void start();

    void processChunk(byte[] chunk, int numBytes);

    void stop();
}
