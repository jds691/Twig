package com.neo.twig.resources;

import java.util.HashMap;

public class ResourceService {
    private HashMap<Integer, Object> resourceCache = new HashMap<>();
    private HashMap<Integer, Integer> referenceCount = new HashMap<>();

    public <T> T getResource(int hashCode) {
        Object cachedResource = resourceCache.get(hashCode);

        if (cachedResource != null) {
            int references = referenceCount.get(hashCode);
            references++;
            referenceCount.put(hashCode, references);
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
            referenceCount.remove(hashCode);
            resourceCache.remove(hashCode);
        }
    }
}
