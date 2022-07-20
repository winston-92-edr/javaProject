package com.mynet.shared.resource.db;

import java.util.concurrent.Callable;

public class DatabaseWork {
    private Callable callable;
    private DatabaseCallback callback;

    public DatabaseWork(Callable callable, DatabaseCallback callback) {
        this.callable = callable;
        this.callback = callback;
    }

    public DatabaseWork(Callable callable) {
        this(callable, null);
    }

    public Callable getCallable() {
        return callable;
    }

    public void setCallable(Callable callable) {
        this.callable = callable;
    }

    public DatabaseCallback getCallback() {
        return callback;
    }

    public void setCallback(DatabaseCallback callback) {
        this.callback = callback;
    }
}
