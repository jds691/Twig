package com.neo.twig.graphics;

import com.neo.twig.Engine;
import com.neo.twig.scene.NodeComponent;
import com.neo.twig.scene.RunInEditor;
import javafx.scene.canvas.GraphicsContext;

@RunInEditor
@SuppressWarnings("unused")
public abstract class RenderComponent extends NodeComponent {
    private final GraphicsService graphicsService;

    public RenderComponent() {
        graphicsService = Engine.getGraphicsService();
    }

    @Override
    public void start() {
        graphicsService.registerRenderComponent(this);
    }

    @Override
    public void destroy() {
        graphicsService.unregisterRenderComponent(this);
    }


    /**
     * Called when the GraphicsService needs to draw the next frame.
     *
     * @param context The GraphicsContext the renderer needs to draw onto.
     * @see GraphicsContext
     * @see GraphicsService
     */
    protected abstract void drawToContext(GraphicsContext context);
}
