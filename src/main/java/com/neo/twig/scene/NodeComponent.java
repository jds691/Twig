package com.neo.twig.scene;

public abstract class NodeComponent implements NodeRunnable {
    private Node control;

    public Node getControl() {
        return control;
    }

    protected void setControl(Node control) {
        this.control = control;
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
