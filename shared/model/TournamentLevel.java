package com.mynet.shared.model;

import java.beans.ConstructorProperties;

public class TournamentLevel {
    private String title;
    private String description;
    private int award;
    private int redeem;
    private int badge;
    private int tournamentId;

    public static final int ENTRANCE_LEVEL_ID = 1;

    @ConstructorProperties({"Title", "Description", "Award", "Redeem", "Badge", "TournamentId"})
    public TournamentLevel(String title, String description, int award, int redeem,int badge, int tournamentId) {
        this.title = title;
        this.description = description;
        this.award = award;
        this.redeem = redeem;
        this.badge = badge;
        this.tournamentId = tournamentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public int getAward() {
        return award;
    }

    public int getRedeem() {
        return redeem;
    }

    public int getBadge() {return badge;}

    public int getTournamentId() {
        return tournamentId;
    }

    @Override
    public String toString() {
        return "TournamentLevel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", award=" + award +
                ", redeem=" + redeem +
                ", badge=" + badge +
                ", tournamentId=" + tournamentId +
                '}';
    }
}
