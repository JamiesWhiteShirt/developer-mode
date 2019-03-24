package com.jamieswhiteshirt.developermode.client.gui;

import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.MultiInputListener;

public interface NestedInputListener extends MultiInputListener {
    default void focusNext() {
        tryFocusNext();
    }

    default void focusPrevious() {
        tryFocusPrevious();
    }

    default boolean tryFocusNext() {
        InputListener currentFocused = getFocused();
        if (currentFocused instanceof NestedInputListener) {
            if (((NestedInputListener) currentFocused).tryFocusNext()) {
                return true;
            }
        }
        int focusedIndex = getInputListeners().indexOf(currentFocused);
        int nextFocusedIndex = focusedIndex + 1;
        if (nextFocusedIndex < getInputListeners().size()) {
            InputListener nextFocused = getFocused(nextFocusedIndex);
            if (nextFocused instanceof MultiInputListener) {
                ((MultiInputListener) nextFocused).focusNext();
            }
            setFocused(nextFocused, focusedIndex);
            return true;
        }
        setFocused(null, focusedIndex);
        return false;
    }

    default boolean tryFocusPrevious() {
        InputListener currentFocused = getFocused();
        if (currentFocused instanceof NestedInputListener) {
            if (((NestedInputListener) currentFocused).tryFocusPrevious()) {
                return true;
            }
        }
        int focusedIndex = getInputListeners().indexOf(currentFocused);
        int nextFocusedIndex = (focusedIndex != -1 ? focusedIndex : getInputListeners().size()) - 1;
        if (nextFocusedIndex >= 0) {
            InputListener nextFocused = getFocused(nextFocusedIndex);
            if (nextFocused instanceof MultiInputListener) {
                ((MultiInputListener) nextFocused).focusPrevious();
            }
            setFocused(nextFocused, focusedIndex);
            return true;
        }
        setFocused(null, focusedIndex);
        return false;
    }

    @Override
    default void onFocusChanged(boolean focus) {
        if (focus) {
            if (getFocused() == null) focusNext();
        } else {
            setFocused(null, getInputListeners().indexOf(getFocused()));
        }
    }

    @Override
    default boolean isPartOfFocusCycle() {
        for (InputListener listener : getInputListeners()) {
            if (listener.isPartOfFocusCycle()) {
                return true;
            }
        }
        return false;
    }
}
