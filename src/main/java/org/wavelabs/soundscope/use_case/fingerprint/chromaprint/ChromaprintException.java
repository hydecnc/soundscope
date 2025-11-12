package org.wavelabs.soundscope.use_cases.fingerprint.chromaprint;

public class ChromaprintException extends RuntimeException {
    public ChromaprintException(String message) {
        super(message);
    }

    public ChromaprintException(String message, Throwable cause) {
        super(message, cause);
    }
}
