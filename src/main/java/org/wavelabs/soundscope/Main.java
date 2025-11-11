package org.wavelabs.soundscope;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.loadAudio(new File("path name here"));
        audioPlayer.startPlayback();
    }
}
