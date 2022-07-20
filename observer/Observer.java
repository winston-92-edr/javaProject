package com.mynet.observer;

public interface Observer {
    void emit();
    void emit(Object data);
    String getId();
}
