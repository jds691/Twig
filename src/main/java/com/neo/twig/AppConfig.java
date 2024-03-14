package com.neo.twig;

import com.neo.twig.annotations.DontSerialize;
import com.neo.twig.config.Config;

import java.net.URL;

@Config(name = "app")
public final class AppConfig {
    public String name;
    public String version;
    @DontSerialize
    public URL initialScene;
}
