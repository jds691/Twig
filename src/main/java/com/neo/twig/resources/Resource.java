package com.neo.twig.resources;

import com.neo.twig.Engine;
import com.neo.twig.events.Event;
import com.neo.twig.logger.Logger;

import java.net.URI;

/**
 * Represents a resource that can be loaded by the engine.
 *
 * @param <T> The type of resource to load.
 */
@SuppressWarnings("unused")
public abstract class Resource<T> {
    protected static final Logger logger = Logger.getFor(Resource.class);
    private final ResourceService resourceService;
    private URI origin;
    private final Object jsonData;
    private T cachedResource;

    private Event<?> resourceNeedsHotReload;

    private Event<Resource<T>> hotReloadRequested;

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

    protected void setReloadPath(URI path) {
        origin = path;
        resourceNeedsHotReload = resourceService.requestFileWatch(path);

        if (resourceNeedsHotReload != null)
            resourceNeedsHotReload.addHandler(this::handleHotReload);
    }

    private void handleHotReload(Object ignored) {
        cachedResource = decodeResource(jsonData);
        hotReloadRequested.emit(this);
    }

    public Event<?> getHotReloadRequestedEvent() {
        return hotReloadRequested;
    }

    public void release() {
        //TODO: Stop watching hot reload files
        resourceService.releaseResource(jsonData.hashCode());
        cachedResource = null;
    }
}
