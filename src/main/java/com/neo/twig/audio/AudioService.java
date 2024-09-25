package com.neo.twig.audio;

import com.neo.twig.audio.FX.FXAudioService;
import com.neo.twig.logger.Logger;

import java.net.URI;
import java.util.ArrayList;

/**
 * Handles audio playback and mixing for the entire engine.
 */
@SuppressWarnings("unused")
public abstract class AudioService {
    private final ArrayList<AudioPlayer> activePlayers = new ArrayList<>();
    private AudioBus audioBusRoot;

    private final static Logger logger = Logger.getFor(AudioService.class);

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

        if (!components[0].equals(audioBusRoot.getName())) {
            logger.logWarning("Initial bus name in audio bus path does not match the mixerTree root!");
        }

        if (components.length == 1)
            return audioBusRoot;

        int currentSearchIndex = 1;
        boolean resultFound = false;
        AudioBus bus = audioBusRoot;

        while (!resultFound) {
            AudioBus nextBus = bus.getChildBus(components[currentSearchIndex]);

            if (nextBus != null) {
                bus = nextBus;
            } else {
                bus = null;
                resultFound = true;
            }

            if (currentSearchIndex == components.length - 1) {
                resultFound = true;
            }
        }

        return bus;
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
