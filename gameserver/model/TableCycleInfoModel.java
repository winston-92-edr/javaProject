package com.mynet.gameserver.model;

import com.mynet.gameserver.enums.TableType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class TableCycleInfoModel {
    private long id;
    private String create;
    private String last;
    private int type;
    private int roomId;

    public TableCycleInfoModel(long id, long create, long last, int type, int roomId) {
        this.id = id;
        this.create = formatDate(create);
        this.last = formatDate(last);
        this.type = type;
        this.roomId = roomId;
    }

    private String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        SimpleDateFormat dateFormatFor = new SimpleDateFormat("yyyy/dd/MM HH:mm:ss");

        return dateFormatFor.format(date);
    }

    @Override
    public String toString() {
        return "{" + "id:" + id + ", create:" + create + ", last:" + last + ", type:" + type + ", roomId:" + roomId + "}";
    }
}
