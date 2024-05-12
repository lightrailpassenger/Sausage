package io.github.lightrailpassenger.sausage.history;

import java.util.Deque;
import java.util.ArrayDeque;

public class History<T> {
    private final Deque<T> undoStack;
    private final Deque<T> redoStack;

    public History() {
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    public void record(T currentState) {
        this.undoStack.push(currentState);
        this.redoStack.clear();
    }

    public boolean canUndo() {
        return !this.undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !this.redoStack.isEmpty();
    }

    public T undo(T currentState) {
        this.redoStack.push(currentState);

        return this.undoStack.pop();
    }

    public T redo(T currentState) {
        this.undoStack.push(currentState);

        return this.redoStack.pop();
    }
}
