package com.mynet.observer;

import com.mynet.shared.types.RequestType;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class RequestObserverCenter implements RequestObservable{
    private static Logger logger = LoggerFactory.getLogger(RequestObserverCenter.class);
    private static RequestObserverCenter INSTANCE;
    private HashMap<RequestType, HashMap<String, ObserverRunnable>> observerHashMap;

    public RequestObserverCenter() {
        this.observerHashMap = new HashMap<>();
    }

    public static RequestObserverCenter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new RequestObserverCenter();
        }

        return INSTANCE;
    }

    @Override
    public void addObserver(RequestType type, ObserverRunnable runnable) {
        observerHashMap.computeIfAbsent(type, k -> new HashMap<>());
        observerHashMap.get(type).put(runnable.getId(), runnable);
    }

    @Override
    public void removeObserver(RequestType type, ObserverRunnable runnable) {
        HashMap<String, ObserverRunnable> runnableSet = observerHashMap.get(type);
        if(runnableSet != null) runnableSet.remove(runnable.getId());
    }

    @Override
    public void emit(RequestType type, Object data) {
        send(type, data);
    }

    @Override
    public void emit(RequestType type) {
        send(type, null);
    }

    private void send(RequestType type, Object data){
        HashMap<String, ObserverRunnable> runnableMap = observerHashMap.get(type);

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
