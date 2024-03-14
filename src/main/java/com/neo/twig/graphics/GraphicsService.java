package com.neo.twig.graphics;

import com.neo.twig.EngineService;
import com.neo.twig.logger.Logger;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public final class GraphicsService implements EngineService {
    private GraphicsConfig config;
    private GraphicsContext drawable;
    private long currentFrameNumber;
    private ArrayList<RenderComponent> renderers;
    private Logger logger;

    private GraphicsService() {
        renderers = new ArrayList<>();
        logger = Logger.getFor(getClass());
    }

    public GraphicsService(GraphicsConfig config) {
        this();

        this.config = config;
    }

    @Override
    public void update(float deltaTime) {
        //REVIEW: Is the renderer going to be able to keep up with more and more objects on screen?
        //It runs fine now due to direct access but yeah...
        drawable.setFill(config.clearColor);
        drawable.fillRect(0, 0, config.width, config.height);

        for (RenderComponent component : renderers) {
            component.drawToContext(drawable);
        }
    }

    public void setGraphicsContext(GraphicsContext context) {
        drawable = context;
    }

    void registerRenderComponent(RenderComponent component) {
        renderers.add(component);
    }

    void unregisterRenderComponent(RenderComponent component) {
        renderers.remove(component);
    }
}
