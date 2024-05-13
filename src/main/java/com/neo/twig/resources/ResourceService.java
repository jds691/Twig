package com.neo.twig.resources;

import com.neo.twig.logger.Logger;

import java.util.HashMap;

public class ResourceService {
    private HashMap<Integer, Object> resourceCache = new HashMap<>();
    private HashMap<Integer, Integer> referenceCount = new HashMap<>();

    private final Logger logger = Logger.getFor(ResourceService.class);

    public <T> T getResource(int hashCode) {
        Object cachedResource = resourceCache.get(hashCode);

        if (cachedResource != null) {
            int references = referenceCount.get(hashCode);
            references++;
            referenceCount.put(hashCode, references);
            logger.logVerbose(String.format("Hit resource cache for '%s', current ref count: %d", hashCode, references));
            return (T) cachedResource;
        }

        return null;
    }

    public void registerResource(int hashCode, Object cachedResource) {
        resourceCache.put(hashCode, cachedResource);
        referenceCount.put(hashCode, 1);
    }

    public void releaseResource(int hashCode) {
        int references = referenceCount.getOrDefault(hashCode, 1);
        references--;

        if (references <= 0) {
            logger.logVerbose(String.format("Resource '%s' no longer referenced. Releasing...", hashCode));
            referenceCount.remove(hashCode);
            resourceCache.remove(hashCode);
        }
    }
}
