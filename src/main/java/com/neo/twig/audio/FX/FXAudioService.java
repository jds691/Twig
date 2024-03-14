package com.neo.twig.audio.FX;

import com.neo.twig.audio.AudioPlayer;
import com.neo.twig.audio.AudioService;

import java.net.URI;

public final class FXAudioService extends AudioService {
    @Override
    public AudioPlayer createOneshotPlayer(URI resource) {
        return new FXOneshotAudioPlayer(resource);
    }

    @Override
    public AudioPlayer createStreamPlayer(URI resource) {
        return new FXStreamAudioPlayer(resource);
    }
}
