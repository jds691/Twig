package com.neo.twig.scene;


import java.util.ArrayList;

/**
 * Represents and maintains a given scene hierarchy.
 */
public final class Scene implements NodeRunnable {
    public final ArrayList<Node> root = new ArrayList<>();
    public boolean paused;

    @Override
    public void start() {
        for (Node node : root) {
            node.start();
        }
    }

    @Override
    public void update(float deltaTime) {
        if (paused)
            return;

        //BUG: Throws a ConcurrentModificationException if a Node is added mid-frame. Potentially introduce a queue
        for (Node node : root) {
            node.update(deltaTime);
        }
    }

    @Override
    public void destroy() {
        for (Node node : root) {
            node.destroy();
        }
    }

    /**
     * @param node
     * @apiNote Automatically calls {@link Node#start()}
     */
    public void addToRoot(Node node) {
        synchronized (root) {
            root.add(node);
            node.start();
        }
    }

    /**
     * @param node
     * @apiNote Automatically calls {@link Node#destroy()}
     */
    public void removeFromRoot(Node node) {
        synchronized (root) {
            root.remove(node);
            node.destroy();
        }
    }

    public boolean rootContains(Node node) {
        synchronized (root) {
            return root.contains(node);
        }
    }
}
