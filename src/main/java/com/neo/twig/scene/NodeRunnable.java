package com.neo.twig.scene;

public interface NodeRunnable {
    void start();

    void update(float deltaTime);

    void destroy();
}
