package com.neo.twig.events;

import java.util.ArrayList;

public final class Event<Args> {
    private final ArrayList<EventHandler<Args>> handlers = new ArrayList<>();

    public void addHandler(EventHandler<Args> handler) {
        handlers.add(handler);
    }

    public void removeHandler(EventHandler<Args> handler) {
        handlers.remove(handler);
    }

    public void emit(Args args) {
        for (EventHandler<Args> handler : handlers) {
            handler.handle(args);
        }
    }
}
