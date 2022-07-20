package com.mynet.shared.model;

import java.util.Map;

public class RoomCountModel {
    private Map<Integer, Integer> counts;

    public RoomCountModel() {
    }

    public RoomCountModel(Map<Integer, Integer> counts) {
        this.counts = counts;
    }

    public void setCounts(Map<Integer, Integer> counts) {
        this.counts = counts;
    }

    public Map<Integer, Integer> getCounts() {
        return counts;
    }
}
