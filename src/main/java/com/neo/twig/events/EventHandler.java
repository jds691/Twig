package com.neo.twig.events;

@FunctionalInterface
public interface EventHandler<Args> {
    void handle(Args args);
}
