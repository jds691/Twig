package com.neo.twig.animation;

import com.neo.twig.graphics.SpriteRenderComponent;
import com.neo.twig.resources.ImageResource;
import org.json.simple.JSONObject;

public class SpriteAnimationTrack extends AnimationTrack<SpriteRenderComponent, SpriteAnimationData> {

    @Override
    boolean processKeyframe(SpriteRenderComponent component, SpriteAnimationData data) {
        component.setSprite(data.getSpriteResource());
        return true;
    }

    @Override
    SpriteAnimationData calculateInterpolatedFrame(SpriteAnimationData lastFrame, SpriteAnimationData nextFrame, float progress) {
        throw new IllegalCallerException("com.neo.twig.animation.SpriteAnimationTrack does not support interpolation");
    }

    @Override
    boolean getDoesSupportInterpolation() {
        return false;
    }
}

class SpriteAnimationData extends KeyFrameData {
    private final ImageResource sprite;

    public SpriteAnimationData(JSONObject json) {
        super(json);
        sprite = new ImageResource(json.get("sprite"));
    }

    public ImageResource getSpriteResource() {
        return sprite;
    }
}
