package com.mynet.tableservice.service;

import com.mynet.shared.model.BasicUserModel;

import java.util.Arrays;
import java.util.HashSet;

public class ServiceUser {
    private BasicUserModel user;
    private Integer[] quickTables;
    private short index;

    public ServiceUser(BasicUserModel user) {
        this.user = user;
        this.quickTables = new Integer[3];
        this.index = 0;
    }

    public void setUser(BasicUserModel user) {
        this.user = user;
    }

    public BasicUserModel getUser() {
        return user;
    }

    public void addQuickTable(int tableId){
        quickTables[index++] = tableId;
        if(index == 3){
            index = 0;
        }
    }

    public boolean isContainsTable(int tableId){
        return Arrays.asList(quickTables).contains(tableId);
    }
}
