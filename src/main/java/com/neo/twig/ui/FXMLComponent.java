package com.neo.twig.ui;

import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.graphics.GraphicsConfig;
import com.neo.twig.logger.Logger;
import com.neo.twig.resources.URLResource;
import com.neo.twig.scene.NodeComponent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Allows for loading and displaying JavaFX scenes defined in FXML files.
 *
 * @apiNote FXMLComponents are drawn on top of the scene and are intended for creating game UI only.
 */
public class FXMLComponent extends NodeComponent {
    private static final Logger logger = Logger.getFor(FXMLComponent.class);
    private FXMLLoader loader;
    private Pane root;
    private Scene uiScene;
    @ForceSerialize
    private URLResource file;

    @Override
    public void start() {
        super.start();

        loader = new FXMLLoader(file.get());
        try {
            GraphicsConfig graphics = Engine.getConfig().graphicsConfig();
            uiScene = new Scene(loader.load(), graphics.width, graphics.height);
            uiScene.setFill(Color.TRANSPARENT);
            root = (Pane) Engine.getSceneService().getStage().getScene().getRoot();
            root.getChildren().add(uiScene.getRoot());
            logger.logInfo("Successfully loaded FXML file");
        } catch (IOException e) {
            logger.logError(String.format("Failed to load FXML file '%s'. Removing component", file.get().getFile()));
            //getControl().removeComponent(this);
        } catch (IllegalStateException e) {
            logger.logError("Failed locate file resource. Ensure it exists. Removing component");
            //getControl().removeComponent(this);
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        if (uiScene != null && !Engine.getShouldQuit())
            root.getChildren().remove(uiScene.getRoot());
    }

    /**
     * Exposes the {@link javafx.scene.Node#setVisible(boolean)} method.
     *
     * @param visible Maps to the boolean parameter of the original method.
     */
    public final void setVisible(boolean visible) {
        uiScene.getRoot().setVisible(visible);
    }

    /**
     * Exposes the {@link FXMLLoader#getController()} method.
     *
     * @param <T> the type of the controller
     * @return the controller associated with the root object
     */
    public <T> T getController() {
        return loader.getController();
    }
}
