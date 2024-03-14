package com.neo.twig;

import com.neo.twig.audio.AudioConfig;
import com.neo.twig.config.ConfigManager;
import com.neo.twig.config.ConfigScope;
import com.neo.twig.graphics.GraphicsConfig;

public record EngineConfig(
        String[] args,
        AppConfig appConfig,
        GraphicsConfig graphicsConfig,
        AudioConfig audioConfig
) {
    void createEngineConfigs() {
        ConfigManager.saveConfig(appConfig(), ConfigScope.Engine);
        ConfigManager.saveConfig(graphicsConfig(), ConfigScope.Engine);
        ConfigManager.saveConfig(audioConfig(), ConfigScope.Engine);
    }
}
