package com.neo.twig.resources;

import com.neo.twig.events.Event;
import com.neo.twig.logger.Logger;

import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;

public class ResourceService {
    private HashMap<Integer, Object> resourceCache = new HashMap<>();
    private HashMap<Integer, Integer> referenceCount = new HashMap<>();

    private boolean useHotReload;
    private HashMap<String, WatchInfo> needToTrackResources;
    private HashMap<String, WatchInfo> trackedResources;

    private final Logger logger = Logger.getFor(ResourceService.class);

    public ResourceService() {
        this(false);

    }

    public ResourceService(boolean useHotReload) {
        this.useHotReload = useHotReload;
        if (useHotReload) {
            needToTrackResources = new HashMap<>();
            trackedResources = new HashMap<>();

            logger.logInfo("Hot reload has been enabled. Resources changed in the target directory should now automatically reload");
        }
    }

    public void update() {
        if (!useHotReload)
            return;

        for (String directory : needToTrackResources.keySet()) {
            trackedResources.put(directory, needToTrackResources.get(directory));
        }

        needToTrackResources.clear();

        for (WatchInfo info : trackedResources.values()) {
            final WatchKey wk;
            try {
                wk = info.getService().take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    //we only register "ENTRY_MODIFY" so the context is always a Path.
                    info.getEvent().emit(null);
                }
                // reset the key
                boolean valid = wk.reset();
                if (!valid) {
                    logger.logVerbose("Key has been unregistered");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

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

    public boolean getUseHotReload() {
        return useHotReload;
    }

    public void registerResource(int hashCode, Object cachedResource) {
        registerResource(hashCode, cachedResource, false);
    }

    public void registerResource(int hashCode, Object cachedResource, boolean overwrite) {
        if (overwrite) {
            resourceCache.remove(hashCode);
            resourceCache.put(hashCode, cachedResource);
        } else {
            resourceCache.put(hashCode, cachedResource);
            referenceCount.put(hashCode, 1);
        }
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

    public Event<?> requestFileWatch(URI file) {
        if (!useHotReload) {
            //logger.logError(String.format("Hot reload is not enabled, unable to watch file '%s'", file.getPath()));
            return null;
        }

        try {
            String fileName = file.toURL().getFile();
            String directory = file.toURL().getPath().replace(fileName, "");
            Path filePath = Path.of(directory);

            if (trackedResources.containsKey(directory))
                return trackedResources.get(directory).getEvent();

            WatchService watcher = filePath.getFileSystem().newWatchService();
            WatchKey watchKey = filePath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            Event<?> fileModifiedEvent = new Event<>();

            WatchInfo info = new WatchInfo(watcher, fileModifiedEvent, fileName);
            needToTrackResources.put(directory, info);

            return info.getEvent();
        } catch (Exception e) {
            logger.logError(String.format("Failed to register '%s' for hot reload", file.getPath()));
            return null;
        }
    }

    private class WatchInfo {
        private WatchService service;
        private Event<?> event;
        private String fileName;

        public WatchInfo(WatchService service, Event<?> event, String fileName) {
            this.service = service;
            this.event = event;
            this.fileName = fileName;
        }

        public WatchService getService() {
            return service;
        }

        public Event<?> getEvent() {
            return event;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
