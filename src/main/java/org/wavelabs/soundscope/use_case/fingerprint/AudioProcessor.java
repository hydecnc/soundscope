package org.wavelabs.soundscope.use_case.fingerprint;

public interface AudioProcessor {
    /**
     * Begins the audio processor.
     */
    void start();

    /**
     * Takes in a byte array audio file and processes the chunk.
     * @param chunk a byte array representing the audio chunk
     * @param numBytes the number of bytes in the byte array
     */
    void processChunk(byte[] chunk, int numBytes);

    /**
     * Stops audio processor.
     */
    void stop();
}
