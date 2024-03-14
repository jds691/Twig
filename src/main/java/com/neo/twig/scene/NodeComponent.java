package com.neo.twig.scene;

public abstract class NodeComponent implements NodeRunnable {
    private Node node;

    public Node getNode() {
        return node;
    }

    protected void setNode(Node node) {
        this.node = node;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void destroy() {

    }
}
