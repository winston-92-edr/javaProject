package com.mynet.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ObserverCenter implements Observable{
    private static Logger logger = LoggerFactory.getLogger(ObserverCenter.class);
    private static ObserverCenter INSTANCE;
    private HashMap<ObserverEvents, HashMap<String, ObserverRunnable>> observerHashMap;

    public ObserverCenter() {
        this.observerHashMap = new HashMap<>();
    }

    public static ObserverCenter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ObserverCenter();
        }

        return INSTANCE;
    }

    @Override
    public void addObserver(ObserverEvents event, ObserverRunnable runnable) {
        observerHashMap.computeIfAbsent(event, k -> new HashMap<>());
        observerHashMap.get(event).put(runnable.getId(), runnable);
    }

    @Override
    public void removeObserver(ObserverEvents event, ObserverRunnable runnable) {
        HashMap<String, ObserverRunnable> runnableSet = observerHashMap.get(event);
        if(runnableSet != null) runnableSet.remove(runnable.getId());
    }

    @Override
    public void emit(ObserverEvents event, Object data) {
        send(event, data);
    }

    @Override
    public void emit(ObserverEvents event) {
        send(event, null);
    }

    private void send(ObserverEvents event, Object data){
        HashMap<String, ObserverRunnable> runnableMap = observerHashMap.get(event);

        if(runnableMap != null){
            try {
                for (ObserverRunnable runnable : runnableMap.values()) {
                    runnable.setData(data);
                    runnable.run();
                }
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }

    }


}
