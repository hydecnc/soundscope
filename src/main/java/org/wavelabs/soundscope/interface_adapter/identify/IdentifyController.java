package org.wavelabs.soundscope.interface_adapter.identify;

import org.wavelabs.soundscope.use_case.identify.IdentifyIB;

public class IdentifyController {
    public final IdentifyIB identifyInteractor;

    public IdentifyController(IdentifyIB identifyInteractor) {
        this.identifyInteractor = identifyInteractor;
    }

    public void identify(){
        identifyInteractor.identify();
    }
}
