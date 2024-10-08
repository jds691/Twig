package com.neo.twig;

import com.neo.twig.annotations.DontSerialize;
import com.neo.twig.config.Config;
import com.neo.twig.resources.ResourcePath;
import javafx.scene.image.Image;

import java.net.URL;

@Config(name = "app")
@SuppressWarnings("unused")
public final class AppConfig {
    public String name;
    public String version;
    @DontSerialize
    public ResourcePath icon;
    @DontSerialize
    public ResourcePath initialScene;
}
