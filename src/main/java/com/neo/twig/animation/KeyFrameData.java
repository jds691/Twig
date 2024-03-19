package com.neo.twig.animation;

import org.json.simple.JSONObject;

public abstract class KeyFrameData {
    float time = 0;

    public KeyFrameData(JSONObject json) {
        time = (float) json.get("time");
    }
}
