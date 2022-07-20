package com.mynet.tableservice.model;

import com.mynet.gameserver.model.TableInfoModel;

import java.util.ArrayList;

public class NotEnoughMoneyErrorModel {
    ArrayList<TableInfoModel> tables;

    public NotEnoughMoneyErrorModel(ArrayList<TableInfoModel> tables) {
        this.tables = tables;
    }
}
