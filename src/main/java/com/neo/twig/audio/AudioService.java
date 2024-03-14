package com.neo.twig.audio;

import com.neo.twig.audio.FX.FXAudioService;

import java.net.URI;
import java.util.ArrayList;

/**
 * Handles audio playback and mixing for the entire engine.
 */
public abstract class AudioService {
    private final ArrayList<AudioPlayer> activePlayers = new ArrayList<>();
    private AudioBus audioBusRoot;

    public static AudioService createService(AudioSubsystem subsystem) {
        switch (subsystem) {
            case FX -> {
                return new FXAudioService();
            }
            default -> {
                return new FXAudioService();
            }
        }
    }

    public void setAudioBusRoot(AudioBus bus) {
        audioBusRoot = bus;
    }

    public AudioBus getAudioBus(String path) {
        String[] components = path.split("/");

        return null;
    }

    protected void registerPlayer(AudioPlayer player) {
        activePlayers.add(player);
    }

    protected void unregisterPlayer(AudioPlayer player) {
        activePlayers.remove(player);
    }

    public void releaseAllPlayers() {
        for (AudioPlayer player : activePlayers) {
            player.release();
        }
    }

    public abstract AudioPlayer createOneshotPlayer(URI resource);

    public abstract AudioPlayer createStreamPlayer(URI resource);
}
