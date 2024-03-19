package com.neo.twig.scene;


import java.util.ArrayList;

/**
 * Represents and maintains a given scene hierarchy.
 */
public final class Scene implements NodeRunnable {
    public final ArrayList<Node> root = new ArrayList<>();
    public boolean paused;

    private boolean isBeingDestroyed;

    @Override
    public void start() {
        for (Node node : root) {
            node.start();
        }
    }

    @Override
    public void update(float deltaTime) {
        if (paused || isBeingDestroyed)
            return;

        //BUG: Throws a ConcurrentModificationException if a Node is added mid-frame. Potentially introduce a queue
        for (Node node : root) {
            node.update(deltaTime);
        }
    }

    @Override
    public void destroy() {
        isBeingDestroyed = true;

        for (Node node : root) {
            node.destroy();
        }
    }

    /**
     * @param node
     * @apiNote Automatically calls {@link Node#start()}
     */
    public void addToRoot(Node node) {
        if (isBeingDestroyed)
            return;

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
        if (isBeingDestroyed)
            return;

        synchronized (root) {
            root.remove(node);
            node.destroy();
        }
    }

    public boolean rootContains(Node node) {
        if (isBeingDestroyed)
            return false;

        synchronized (root) {
            return root.contains(node);
        }
    }
}
