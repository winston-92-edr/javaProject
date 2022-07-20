package com.mynet.test;

import com.mynet.observer.ObserverRunnable;
import com.mynet.observer.ObserverCenter;
import com.mynet.observer.ObserverEvents;

public class ObserverTest {

    public ObserverTest() {
        ObserverCenter.getInstance().addObserver(ObserverEvents.CHANNEL_ACTIVE, new ChannelActive());
    }

    private class ChannelActive extends ObserverRunnable{

        private String message;
        private String id;

        public ChannelActive() {
            this.id = "ChannelActive_" + System.currentTimeMillis();;
        }

        @Override
        protected void setData(Object data) {
            this.message = (String) data;
        }

        @Override
        protected String getId() {
            return id;
        }

        @Override
        public void run() {
            System.out.println("CHANNEL_ACTIVE: " + message);
        }
    }
}
