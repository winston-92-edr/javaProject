package com.mynet.shared.model;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TournamentModel {
    private final int tournamentId;
    private String title;
    private String description;
    private int failPoint;
    private int sideCount;
    private int maxTryCount;
    private int cost;
    private String place;
    private String awardType;
    private long startDate;
    private long endDate;
    private int type;
    private List<TournamentLevel> levels;
    private boolean active;

    @ConstructorProperties({"id", "TournamentId", "Title", "Description", "FailPoint", "SideCount", "MaxTryCount", "Cost", "Place", "AwardType", "Type", "StartTime", "EndTime"})
    public TournamentModel(int id, int tournamentId, String title, String description, int failPoint, int sideCount, int maxTryCount, int cost, String place, String awardType, int type, Date startTime, Date endTime) {
        this.tournamentId = tournamentId;
        this.title = title;
        this.description = description;
        this.failPoint = failPoint;
        this.sideCount = sideCount;
        this.maxTryCount = maxTryCount;
        this.cost = cost;
        this.place = place;
        this.awardType = awardType;
        this.type = type;
        this.startDate = startTime == null ? -1 : startTime.getTime();
        this.endDate = endTime == null ? -1 : endTime.getTime();
        this.levels = new ArrayList<>();
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getFailPoint() {
        return failPoint;
    }

    public int getSideCount() {
        return sideCount;
    }

    public int getMaxTryCount() {
        return maxTryCount;
    }

    public int getCost() {
        return cost;
    }

    public String getPlace() {
        return place;
    }

    public String getAwardType() {
        return awardType;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public int getType() {
        return type;
    }

    public List<TournamentLevel> getLevels() {
        return levels;
    }

    public boolean isActive() {
        return active;
    }

    public void setLevels(List<TournamentLevel> levels) {
        this.levels = levels;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "TournamentModel{" +
                "tournamentId=" + tournamentId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", failPoint=" + failPoint +
                ", sideCount=" + sideCount +
                ", maxTryCount=" + maxTryCount +
                ", cost=" + cost +
                ", place='" + place + '\'' +
                ", awardType='" + awardType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", type=" + type +
                ", levels=" + levels +
                ", active=" + active +
                '}';
    }

    public TournamentLevel getLevel(int levelId) {
        int index = levelId - TournamentLevel.ENTRANCE_LEVEL_ID;
        if (getMaxLevelId() <= index) {
            return null;
        }

        return levels.get(index);
    }

    public int getMaxLevelId() {
        return levels.size() - 1 + TournamentLevel.ENTRANCE_LEVEL_ID;
    }
}
