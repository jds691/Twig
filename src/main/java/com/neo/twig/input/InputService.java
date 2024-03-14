package com.neo.twig.input;

import com.neo.twig.EngineService;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

//TODO: wasPressed and wasReleased functions do not work correctly
public final class InputService implements EngineService {
    private final InputState[] states;
    private final EventHandler<KeyEvent> keyEventEventHandler;
    private final EventHandler<MouseEvent> mouseEventEventHandler;

    public InputService() {
        //Last, current and next frame
        states = new InputState[3];
        states[0] = new InputState();
        states[1] = new InputState();
        states[2] = new InputState();
        keyEventEventHandler = new KeyEventHandler();
        mouseEventEventHandler = new MouseEventHandler();
    }

    public EventHandler<KeyEvent> getKeyEventHandler() {
        return keyEventEventHandler;
    }

    public EventHandler<MouseEvent> getMouseEventHandler() {
        return mouseEventEventHandler;
    }

    public boolean wasKeyPressed(KeyCode key) {
        return states[1].getKeyState(key) && !states[2].getKeyState(key);
    }

    public boolean isKeyHeld(KeyCode key) {
        return states[1].getKeyState(key) && states[2].getKeyState(key);
    }

    public boolean wasKeyReleased(KeyCode key) {
        return !states[1].getKeyState(key) && states[2].getKeyState(key);
    }

    public boolean wasMouseButtonPressed(MouseButton button) {
        return states[1].getMouseButtonState(button) && !states[2].getMouseButtonState(button);
    }

    public boolean isMouseButtonHeld(MouseButton button) {
        return states[1].getMouseButtonState(button) && states[2].getMouseButtonState(button);
    }

    public boolean wasMouseButtonReleased(MouseButton button) {
        return !states[1].getMouseButtonState(button) && states[2].getMouseButtonState(button);
    }

    @Override
    public void update(float deltaTime) {
        states[2] = states[1];
        states[1] = states[0];
    }

    //TODO: Doesn't seem to properly release keys
    private class KeyEventHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent keyEvent) {
            KeyCode code = keyEvent.getCode();
            states[0].setKeyState(code, keyEvent.getEventType() == KeyEvent.KEY_PRESSED);
        }
    }

    private class MouseEventHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            MouseButton button = mouseEvent.getButton();

            states[0].setMouseButtonState(button, mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED);
        }
    }
}
