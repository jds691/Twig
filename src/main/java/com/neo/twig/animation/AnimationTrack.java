package com.neo.twig.animation;

import com.neo.twig.scene.NodeComponent;

//TODO: Determine how to pass keyframe data into each track, specifically the type of the data to pass in

/**
 * Responsible for receiving animation data for a specific component or action and processing the data appropriately.
 *
 * <p>
 * It is possible to create custom animation tracks to animate custom components the engine does not accommodate for.
 * </p>
 *
 * @param <Component> The component this track should act upon.
 * @param <FrameData> The data that this track receives when it needs to process a frame.
 */
public abstract class AnimationTrack<Component extends NodeComponent, FrameData extends KeyFrameData> {
    /**
     * Called in order to process the next explicit keyframe.
     *
     * @return Whether the frame has been correctly processed or not.
     */
    abstract boolean processKeyframe(Component component, FrameData data);

    /**
     * Called when the engine needs to calculate an interpolated frame, if supported by the track.
     *
     * @param lastFrame The last explicitly declared keyframe.
     * @param nextFrame The next explicitly declared keyframe.
     * @param progress  Value between 0.0-1.0 representing the ratio of time between the two frames.
     */
    abstract FrameData calculateInterpolatedFrame(FrameData lastFrame, FrameData nextFrame, float progress);

    /**
     * @return Whether the track supports calculating interpolated frames.
     */
    abstract boolean getDoesSupportInterpolation();
}
