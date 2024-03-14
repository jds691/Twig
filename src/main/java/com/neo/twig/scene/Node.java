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

//TODO: Allow for controls to add or remove themselves from the scene hierarchy
//TODO: Implement queue for adding and deleting things mid update
public final class Node implements NodeRunnable {
    private final int INITIAL_COMPONENT_CAPACITY = 32;
    private final ArrayList<NodeComponent> components;
    private final ArrayList<Node> children;
    private String name;
    private boolean hasStarted;

    public Node() {
        super();

        components = new ArrayList<>(INITIAL_COMPONENT_CAPACITY);
        children = new ArrayList<>();

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
        children = ref.children;
    }

    public void instantiate() {
        instantiate(null);
    }

    public void instantiate(Node parent) {
        if (parent == null) {
            Engine.getSceneService().getActiveScene().addToRoot(this);
        } else {
            parent.addChild(this);
        }
    }

    @Override
    public void start() {
        hasStarted = true;

        for (NodeComponent component : components) {
            component.start();
        }

        for (Node node : children) {
            node.start();
        }
    }

    @Override
    public void update(float deltaTime) {
        for (NodeComponent component : components) {
            component.update(deltaTime);
        }

        for (Node node : children) {
            node.update(deltaTime);
        }
    }

    @Override
    public void destroy() {
        for (NodeComponent component : components) {
            component.destroy();
        }

        for (Node node : children) {
            node.destroy();
        }

        Scene activeScene = Engine.getSceneService().getActiveScene();

        if (activeScene.rootContains(this)) {
            activeScene.removeFromRoot(this);
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

    public void addChild(Node node) {
        children.add(node);

        if (hasStarted)
            node.start();
    }

    public void removeChild(Node node) {
        children.remove(node);

        //REVIEW: Assumes children never need to live on their own
        node.destroy();
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void saveConfig() {
        ConfigManager.saveConfig(this);
    }

    public void loadConfig() {
        ConfigManager.loadConfig(this);
    }
}
