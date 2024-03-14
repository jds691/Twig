package com.neo.twig;

import com.neo.twig.audio.AudioConfig;
import com.neo.twig.graphics.GraphicsConfig;

public record EngineConfig(
        String[] args,
        AppConfig appConfig,
        GraphicsConfig graphicsConfig,
        AudioConfig audioConfig
) {

}
