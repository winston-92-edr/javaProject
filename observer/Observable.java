package com.mynet.observer;

import java.util.concurrent.Callable;

public interface Observable {
    void addObserver(ObserverEvents event, ObserverRunnable runnable);
    void removeObserver(ObserverEvents event, ObserverRunnable runnable);
    void emit(ObserverEvents event, Object data);
    void emit(ObserverEvents event);
}
