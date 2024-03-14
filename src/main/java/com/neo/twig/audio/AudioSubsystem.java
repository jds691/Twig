package com.neo.twig.audio;

public enum AudioSubsystem {
    /**
     * Automatically determines the best subsystem based on the current platform.
     */
    Auto,
    /**
     * Uses the JavaFX audio APIs.
     * <p>
     * It has a number of known issues and should only be used for compatibility reasons.
     * </p>
     */
    FX
}
