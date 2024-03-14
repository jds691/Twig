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
        for (Node control : root) {
            control.start();
        }
    }

    @Override
    public void update(float deltaTime) {
        if (paused)
            return;

        //BUG: Throws a ConcurrentModificationException if a Control is added mid-frame. Potentially introduce a queue
        for (Node control : root) {
            control.update(deltaTime);
        }
    }

    @Override
    public void destroy() {
        for (Node control : root) {
            control.destroy();
        }
    }

    /**
     * @param control
     * @apiNote Automatically calls {@link Node#start()}
     */
    public void addToRoot(Node control) {
        synchronized (root) {
            root.add(control);
            control.start();
        }
    }

    /**
     * @param control
     * @apiNote Automatically calls {@link Node#destroy()}
     */
    public void removeFromRoot(Node control) {
        synchronized (root) {
            root.remove(control);
            control.destroy();
        }
    }

    public boolean rootContains(Node node) {
        synchronized (root) {
            return root.contains(node);
        }
    }
}
