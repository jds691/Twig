package com.neo.twig.resources;

import com.neo.twig.Engine;
import com.neo.twig.logger.Logger;

import java.nio.file.Path;

public class ResourcePath {
    private final String assetPath;
    private Path resolvedPath;

    public ResourcePath(String assetPath) {
        this.assetPath = assetPath;
        //resolvedPath = Path.of(Engine.getConfig().resourcesConfig().resourceDirectory, assetPath);
    }

    public String getAssetPath() {
        return assetPath;
    }

    public Path getPath() {
        if (resolvedPath == null) {
            resolvedPath = Path.of(Engine.getConfig().resourcesConfig().resourceDirectory, assetPath);
            Logger.getFor(ResourcePath.class).logDebug(resolvedPath.toString());
        }

        return resolvedPath;
    }

    public static Path resolveAssetPath(String assetPath) {
        return Path.of(Engine.getConfig().resourcesConfig().resourceDirectory, assetPath);
    }
}
