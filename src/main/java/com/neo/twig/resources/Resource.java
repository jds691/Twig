package com.neo.twig.resources;

import com.neo.twig.Engine;
import com.neo.twig.logger.Logger;

/**
 * Represents a resource that can be loaded by the engine.
 *
 * @param <T> The type of resource to load.
 */
public abstract class Resource<T> {
    protected static final Logger logger = Logger.getFor(Resource.class);
    private final ResourceService resourceService;
    private final Object jsonData;
    private T cachedResource;

    protected Resource(Object json) {
        resourceService = Engine.getResourceService();
        jsonData = json;
    }

    public T get() {
        if (cachedResource != null)
            return cachedResource;

        cachedResource = resourceService.getResource(jsonData.hashCode());
        if (cachedResource == null) {
            cachedResource = decodeResource(jsonData);
            resourceService.registerResource(jsonData.hashCode(), cachedResource);
        }

        return cachedResource;
    }

    /**
     * Called by the engine when loading and caching the resource.
     *
     * @param jsonObject A JSONObject with at minimum a key called "class". This key should point to a class name which should be used for getting the resource.
     * @return The parsed resource of type T
     */
    protected abstract T decodeResource(Object jsonObject);

    public void release() {
        resourceService.releaseResource(jsonData.hashCode());
        cachedResource = null;
    }
}
