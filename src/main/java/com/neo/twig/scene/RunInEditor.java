package com.neo.twig.scene;


import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * When a class is annotated with this it still receives scene events even when running in an editor program.
 */
@Target(ElementType.TYPE)
public @interface RunInEditor {
}
