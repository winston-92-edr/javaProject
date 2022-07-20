package com.mynet.questservice.quests.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class QuestSeasonModel {
    private int id;
    private Date startDate;
    private Date endDate;
    private int day;

    public QuestSeasonModel(int id, String startDate, String endDate) {

        this.id = id;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.startDate = format.parse(startDate);
            this.endDate = format.parse(endDate);
            this.day = calculateDay();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public long getStartTime(){
        return startDate.getTime();
    }

    public long getEndTime(){
        return endDate.getTime();
    }

    public int getDay() {
        this.day = calculateDay();
        return this.day;
    }

    public int calculateDay(){
        long diff = System.currentTimeMillis() - this.startDate.getTime();
        long longDay = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return (int) longDay + 1;
    }

    @Override
    public String toString() {
        return "QuestSeasonModel{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestSeasonModel that = (QuestSeasonModel) o;
        return id == that.id &&
                startDate.equals(that.startDate) &&
                endDate.equals(that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate);
    }
}
