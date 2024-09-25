package com.neo.twig.audio.FX;

import com.neo.twig.audio.AudioPlayer;
import javafx.scene.media.AudioClip;

import java.net.URI;

public class FXOneshotAudioPlayer extends AudioPlayer {
    protected AudioClip audioClip;

    public FXOneshotAudioPlayer(URI resource) {
        super(resource);

        audioClip = new AudioClip(resource.toString());

        if (onReadyCallback != null)
            onReadyCallback.run();
    }

    @Override
    public void play() {
        if (looping) {
            audioClip.setCycleCount(AudioClip.INDEFINITE);
        } else {
            audioClip.setCycleCount(1);
        }

        audioClip.setVolume(getVolume());
        audioClip.play();
    }

    @Override
    public void pause() {
    }

    @Override
    public void stop() {
        audioClip.stop();
    }

    @Override
    public void release() {
        super.release();

        audioClip = null;
    }

    @Override
    public void setVolume(float volume) {
        super.setVolume(volume);

        audioClip.setVolume(getVolume());
    }
}
