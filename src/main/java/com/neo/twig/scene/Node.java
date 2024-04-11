package com.neo.twig.scene;

import com.neo.twig.Engine;
import com.neo.twig.config.ConfigManager;
import com.neo.twig.resources.URLResource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public final class Node implements NodeRunnable {
    private final int INITIAL_COMPONENT_CAPACITY = 32;
    private final ArrayList<NodeComponent> components;
    private String name;
    private boolean hasStarted;

    public Node() {
        super();

        components = new ArrayList<>(INITIAL_COMPONENT_CAPACITY);

        loadConfig();
    }

    public Node(URLResource twigFile) {
        super();

        URL resource = twigFile.get();
        JSONParser parser = new JSONParser();

        FileReader file;
        Node ref;
        try {
            file = new FileReader(resource.getFile());
            JSONObject nodeJSON = (JSONObject) parser.parse(file);
            ref = SceneLoader.parseNode(nodeJSON, true);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        name = ref.name;
        components = ref.components;
    }

    public void instantiate() {
        Engine.getSceneService().getActiveScene().addToRoot(this);
    }

    @Override
    public void start() {
        hasStarted = true;

        for (NodeComponent component : components) {
            component.start();
        }
    }

    @Override
    public void update(float deltaTime) {
        for (NodeComponent component : components) {
            component.update(deltaTime);
        }
    }

    @Override
    public void destroy() {
        destroy(false);
    }

    void destroy(boolean deconstruct) {
        Scene activeScene = Engine.getSceneService().getActiveScene();
        if (!activeScene.rootContains(this)) {
            return;
        }

        if (!deconstruct) {
            activeScene.removeFromRoot(this);
        } else {
            for (NodeComponent component : components) {
                component.destroy();
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addComponent(NodeComponent component) {
        if (!hasComponent(component.getClass())) {
            component.setNode(this);

            if (hasStarted)
                component.start();

            components.add(component);
        }
    }

    public void removeComponent(NodeComponent component) {
        if (components.contains(component)) {
            components.remove(component);

            if (hasStarted)
                component.destroy();
        }
    }

    public ArrayList<NodeComponent> getComponents() {
        return components;
    }

    public <T extends NodeComponent> T getComponent(Class<T> component) {
        for (NodeComponent registeredComponent : components) {
            if (registeredComponent.getClass() == component) {
                return (T) registeredComponent;
            }
        }

        return null;
    }

    public boolean hasComponent(Class<? extends NodeComponent> component) {
        for (NodeComponent registeredComponent : components) {
            if (registeredComponent.getClass() == component)
                return true;
        }

        return false;
    }

    public void saveConfig() {
        ConfigManager.saveConfig(this);
    }

    public void loadConfig() {
        ConfigManager.loadConfig(this);
    }
}
