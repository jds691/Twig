package com.neo.twig.input;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

/**
 * Represents the current state of inputs for a given frame.
 *
 * <p>Contains the state for the keyboard, mouse buttons and mouse position.</p>
 */
class InputState {
    private boolean[] keys;
    private boolean[] mouseButtons;

    public InputState() {
        keys = new boolean[KeyCode.values().length];
        mouseButtons = new boolean[MouseButton.values().length];
    }

    public void setKeyState(KeyCode key, boolean pressed) {
        keys[key.ordinal()] = pressed;
    }

    public void setMouseButtonState(MouseButton button, boolean pressed) {
        mouseButtons[button.ordinal()] = pressed;
    }

    public boolean getKeyState(KeyCode key) {
        return keys[key.ordinal()];
    }

    public boolean getMouseButtonState(MouseButton button) {
        return mouseButtons[button.ordinal()];
    }
}
