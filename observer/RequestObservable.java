package com.mynet.observer;

import com.mynet.shared.types.RequestType;

public interface RequestObservable {
    void addObserver(RequestType event, ObserverRunnable runnable);
    void removeObserver(RequestType event, ObserverRunnable runnable);
    void emit(RequestType event, Object data);
    void emit(RequestType event);
}
