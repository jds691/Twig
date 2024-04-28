package com.neo.twig.graphics;

import com.neo.twig.annotations.DontSerialize;
import com.neo.twig.config.Config;
import javafx.scene.paint.Color;

/**
 * Represents the graphics settings of the engine.
 * <p>
 * It is recommended to save graphics settings under the {@link com.neo.twig.config.ConfigScope} scope.
 */
@Config(name = "graphics")
@SuppressWarnings("unused")
public class GraphicsConfig {
    public int width = 320;
    public int height = 240;
    @DontSerialize
    public boolean allowWindowResizing = false;
    public WindowMode mode = WindowMode.Standard;
    @DontSerialize
    public Color clearColor = Color.BLACK;
}
