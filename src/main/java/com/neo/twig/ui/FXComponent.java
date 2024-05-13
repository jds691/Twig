package com.neo.twig.ui;

import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.graphics.GraphicsConfig;
import com.neo.twig.logger.Logger;
import com.neo.twig.resources.StylesheetResource;
import com.neo.twig.scene.NodeComponent;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 * Allows creating JavaFX UI in engine by using the Java API instead of .fxml files.
 *
 * <p>
 * The subclass of this component should also be used as the Controller for the UI.
 * </p>
 */
@SuppressWarnings("unused")
public abstract class FXComponent extends NodeComponent {
    private Logger logger;
    private Pane root;
    private Parent uiRoot;

    @ForceSerialize
    private StylesheetResource[] stylesheets = new StylesheetResource[0];

    @Override
    public void start() {
        super.start();

        logger = Logger.getFor(getClass());
        GraphicsConfig graphics = Engine.getConfig().graphicsConfig();
        uiRoot = generateFXScene();
        uiRoot.getStylesheets().clear();

        for (StylesheetResource stylesheet : stylesheets) {
            String stylesheetLocation = stylesheet.get();
            uiRoot.getStylesheets().add(stylesheetLocation);
            logger.logVerbose("Loaded stylesheet: '" + stylesheetLocation + "'");
        }

        root = (Pane) Engine.getSceneService().getStage().getScene().getRoot();
        root.getChildren().add(uiRoot);
        logger.logInfo("Successfully generated FX UI");
    }

    @Override
    public void destroy() {
        super.destroy();

        if (uiRoot != null && !Engine.getShouldQuit())
            root.getChildren().remove(uiRoot);
    }

    /**
     * Exposes the {@link javafx.scene.Node#setVisible(boolean)} method.
     *
     * @param visible Maps to the boolean parameter of the original method.
     */
    public void setVisible(boolean visible) {
        uiRoot.setVisible(visible);
    }

    /**
     * Called by the Engine to generate the UI.
     *
     * @return Built UI
     */
    public abstract Parent generateFXScene();

    private void handleStylesheetHotReload(StylesheetResource resource) {
        //TODO: Make preserve CSS hierarchy
        uiRoot.getStylesheets().remove(resource.get());
        uiRoot.getStylesheets().add(resource.get());
    }
}
