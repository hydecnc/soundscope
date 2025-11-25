package org.wavelabs.soundscope.use_case.identify;

import org.wavelabs.soundscope.data_access.AcousticIDIdentify;
import org.wavelabs.soundscope.entity.Song;

public class IdentifyInteractor implements IdentifyIB{
    private final Song song;

    public IdentifyInteractor(Song song) {this.song = song;}

    @Override
    public void identify(int duration){
        final IdentifyDAI identifier = new AcousticIDIdentify();
        final Song.SongMetadata metadata = identifier.getClosestMatchMetadata(song.getFingerprint(), duration);

        song.setMetadata(metadata);
    }
}
