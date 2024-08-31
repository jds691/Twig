package com.neo.twig.resources;

import com.neo.twig.config.Config;

@Config(name = "resources")
@SuppressWarnings("unused")
public class ResourcesConfig {
    public String resourceDirectory;
    public ResourcePath path = new ResourcePath("test");
}
