package io.github.lightrailpassenger.sausage.history;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class HistoryIntervalRecorder<T> implements ActionListener {
    private final History<T> history;
    private final Timer timer;
    private final State<T> state;
    private T lastState;
    private T pendingRecordState;

    public static interface State<T> {
        public T getInitialState();
        public T get();
        public void set(T value);
    }

    public HistoryIntervalRecorder(State<T> state, int delay) {
        this.history = new History<T>();
        this.state = state;
        this.timer = new Timer(delay, this);
        this.timer.start();
        this.pendingRecordState = state.getInitialState();
    }

    public HistoryIntervalRecorder(State<T> state) {
        this(state, 5 * 1000); // 5 seconds
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        T latestState = state.get();

        if (latestState != null && !latestState.equals(lastState)) {
            if (pendingRecordState != null) {
                history.record(pendingRecordState);
                lastState = latestState;
            }

            pendingRecordState = latestState;
        }
    }

    public void forceRecord() {
        T currentState = state.get();

        if (currentState != null) {
            history.record(currentState);
            lastState = currentState;
            pendingRecordState = null;
            timer.restart();
        }
    }

    public boolean canUndo() {
        return this.history.canUndo();
    }

    public boolean canRedo() {
        return this.history.canRedo();
    }

    public void undo() {
        T currentState = state.get();
        T latestState = history.undo(currentState);

        lastState = latestState;
        state.set(lastState);
    }

    public void redo() {
        T currentState = state.get();
        T latestState = history.redo(currentState);

        lastState = latestState;
        state.set(lastState);
    }
}
