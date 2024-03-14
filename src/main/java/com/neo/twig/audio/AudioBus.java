package com.neo.twig.audio;

import java.util.ArrayList;

public class AudioBus {
    private AudioBus parentBus;
    private ArrayList<AudioBus> childBuses;
    private String name;
    private float m_Volume = 1f;

    public AudioBus() {
        name = "";
        childBuses = new ArrayList<>();
    }

    public AudioBus(String name) {
        this();

        setName(name);
    }

    public void addChildBus(AudioBus bus) {
        childBuses.add(bus);
        bus.parentBus = this;
    }

    public AudioBus getChildBus(String name) {
        for (AudioBus bus : childBuses) {
            if (bus.getName().equals(name)) {
                return bus;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getVolume() {
        return m_Volume;
    }

    public void setVolume(float volume) {
        m_Volume = volume;
    }

    public float getMixedVolume() {
        if (parentBus != null)
            return parentBus.getMixedVolume() * m_Volume;
        else
            return m_Volume;
    }
}
