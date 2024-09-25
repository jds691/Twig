package com.neo.twig.audio;

import com.neo.twig.Engine;

import java.net.URI;

@SuppressWarnings("unused")
public abstract class AudioPlayer {
    protected Runnable onReadyCallback;
    protected boolean looping;
    private AudioBus audioBus;
    private float volume = 1;

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
        return audioBus;
    }

    public final void setAudioBus(String busPath) {
        setAudioBus(Engine.getAudioService().getAudioBus(busPath));
    }

    public void setAudioBus(AudioBus bus) {
        audioBus = bus;
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

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        if (audioBus != null) {
            return audioBus.getMixedVolume() * volume;
        } else {
            return volume;
        }
    }

    enum State {
        PLAYING,
        PAUSED,
        STOPPED
    }
}
