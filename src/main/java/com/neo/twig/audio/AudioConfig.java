package com.neo.twig.audio;

import com.neo.twig.annotations.DontSerialize;
import com.neo.twig.config.Config;

@Config(name = "audio")
@SuppressWarnings("unused")
public class AudioConfig {
    @DontSerialize
    public AudioBus mixerTree;

    public AudioSubsystem subsystem = AudioSubsystem.Auto;
}
