package com.neo.twig.audio;

import com.neo.twig.Engine;

import java.net.URI;

public abstract class AudioPlayer {
    protected Runnable onReadyCallback;
    protected boolean looping;

    public AudioPlayer() {
    }

    public AudioPlayer(URI resource) {
        this();
    }

    public abstract void play();

    public abstract void pause();

    public abstract void stop();

    public void release() {
        stop();

        Engine.getAudioService().unregisterPlayer(this);
    }

    public AudioBus getAudioBus() {
        return null;
    }

    public void setAudioBus(AudioBus bus) {

    }

    public boolean getLooping() {
        return looping;
    }

    public void setLooping(boolean loop) {
        looping = loop;
    }

    public void setOnReadyCallback(Runnable callback) {
        onReadyCallback = callback;
    }

    enum State {
        PLAYING,
        PAUSED,
        STOPPED
    }
}
