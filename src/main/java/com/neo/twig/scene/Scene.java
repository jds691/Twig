package com.neo.twig.scene;


import java.util.ArrayList;

/**
 * Represents and maintains a given scene hierarchy.
 */
public final class Scene implements NodeRunnable {
    private final ArrayList<Node> removeQueue = new ArrayList<>();
    private final ArrayList<Node> addQueue = new ArrayList<>();
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

        for (Node node : removeQueue) {
            node.destroy(true);
            root.remove(node);
        }
        removeQueue.clear();

        for (Node node : addQueue) {
            root.add(node);
            node.start();
        }
        addQueue.clear();
    }

    @Override
    public void destroy() {
        isBeingDestroyed = true;

        for (Node node : root) {
            node.destroy(true);
        }
    }

    /**
     * @param node
     * @apiNote Automatically calls {@link Node#start()}
     */
    public void addToRoot(Node node) {
        if (isBeingDestroyed)
            return;

        addQueue.add(node);
    }

    /**
     * @param node
     * @apiNote Automatically calls {@link Node#destroy()}
     */
    public void removeFromRoot(Node node) {
        if (isBeingDestroyed)
            return;

        removeQueue.add(node);
    }

    public boolean rootContains(Node node) {
        if (isBeingDestroyed)
            return false;

        synchronized (root) {
            return root.contains(node);
        }
    }
}
