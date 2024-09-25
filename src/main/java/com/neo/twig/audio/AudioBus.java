package com.neo.twig.audio;

import com.neo.twig.events.Event;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class AudioBus {
    private AudioBus parentBus;
    private ArrayList<AudioBus> childBuses;
    private String name;
    private float volume = 1f;
    private Event<Float> onVolumeChanged = new Event<Float>();

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
        onVolumeChanged.addHandler(bus::handleParentBusVolumeChange);
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
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
        onVolumeChanged.emit(volume);
    }

    public float getMixedVolume() {
        if (parentBus != null)
            return parentBus.getMixedVolume() * volume;
        else
            return volume;
    }

    private void handleParentBusVolumeChange(float rawVolume) {
        onVolumeChanged.emit(getVolume());
    }

    public Event<Float> getOnVolumeChangedEvent() {
        return onVolumeChanged;
    }
}
