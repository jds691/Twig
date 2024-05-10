package com.neo.twig.scene;

import com.neo.twig.EngineService;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;

/**
 * Handles and updates the scene lifecycle.
 */
@SuppressWarnings("unused")
public final class SceneService implements EngineService {
    private final ArrayList<Scene> registeredScenes;
    private Stage stage;

    public SceneService() {
        registeredScenes = new ArrayList<>();
    }

    @Override
    public void update(float deltaTime) {
        for (Scene scene : registeredScenes) {
            scene.update(deltaTime);
        }
    }

    public void destroy() {
        destroyRegisteredScenes();
    }


    /**
     * Changes the active scene to be the scene located at the URL.
     *
     * @param resource URL path to the scene resource.
     */
    public void setScene(URL resource) {
        getStage().getScene().getStylesheets().clear();
        Scene scene = SceneLoader.loadFrom(resource);

        destroyRegisteredScenes();

        registeredScenes.clear();
        registeredScenes.add(scene);
        scene.start();
    }

    /**
     * Adds the scene located at the URL to the list of registered scenes.
     *
     * <p>It will not be set as the active scene.</p>
     *
     * @param resource URL path to the scene resource.
     */
    public void addScene(URL resource) {
        Scene scene = SceneLoader.loadFrom(resource);
        registeredScenes.add(scene);
        scene.start();
    }

    /**
     * Gets the currently active scene that all scene related calls are sent to.
     *
     * @return The active <code>Scene</code>.
     */
    public Scene getActiveScene() {
        //TODO: Allow for changing the active scene
        return registeredScenes.getLast();
    }

    private void destroyRegisteredScenes() {
        for (Scene scene : registeredScenes) {
            scene.destroy();
        }
    }

    public Stage getStage() {
        return stage;
    }

    //TODO: Fix access visibility
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
