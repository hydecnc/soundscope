package org.wavelabs.soundscope.interface_adapter.fingerprint;

import org.wavelabs.soundscope.interface_adapter.ViewModel;

public class FingerprintViewModel extends ViewModel<FingerprintState> {
    public FingerprintViewModel() {
        super("fingerprint");
        setState(new FingerprintState());
    }
}
