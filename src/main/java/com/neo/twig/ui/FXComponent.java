package com.neo.twig.ui;

import com.neo.twig.Engine;
import com.neo.twig.graphics.GraphicsConfig;
import com.neo.twig.logger.Logger;
import com.neo.twig.scene.NodeComponent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Allows creating JavaFX UI in engine by using the Java API instead of .fxml files.
 *
 * <p>
 * The subclass of this component should also be used as the Controller for the UI.
 * </p>
 */
@SuppressWarnings("unused")
public abstract class FXComponent extends NodeComponent {
    private static final Logger logger = Logger.getFor(FXComponent.class);
    private Pane root;
    private Scene uiScene;

    @Override
    public void start() {
        super.start();

        GraphicsConfig graphics = Engine.getConfig().graphicsConfig();
        uiScene = new Scene(generateFXScene(), graphics.width, graphics.height);
        uiScene.setFill(Color.TRANSPARENT);
        root = (Pane) Engine.getSceneService().getStage().getScene().getRoot();
        root.getChildren().add(uiScene.getRoot());
        logger.logInfo("Successfully generated FX UI.");
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
    public void setVisible(boolean visible) {
        uiScene.getRoot().setVisible(visible);
    }

    /**
     * Called by the Engine to generate the UI.
     *
     * @return Built UI
     */
    public abstract Parent generateFXScene();
}
