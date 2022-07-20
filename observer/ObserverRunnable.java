package com.mynet.observer;

public abstract class ObserverRunnable implements Runnable {
    protected abstract void setData(Object data);
    protected abstract String getId();
}
