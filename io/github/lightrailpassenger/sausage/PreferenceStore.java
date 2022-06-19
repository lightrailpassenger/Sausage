package io.github.lightrailpassenger.sausage;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class PreferenceStore {
    private static final PreferenceStore STORE_INSTANCE = new PreferenceStore();

    private AtomicInteger untitledCounter = new AtomicInteger(1);
    private List<SausageFrame> sausageFrameList = new ArrayList<>();

    static PreferenceStore getInstance() {
        return STORE_INSTANCE;
    }

    int getAndIncrementUntitledCounter() {
        return this.untitledCounter.getAndIncrement();
    }

    void addFrame(SausageFrame frame) {
        this.sausageFrameList.add(frame);
    }
}
