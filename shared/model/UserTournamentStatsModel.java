package com.mynet.shared.model;

import java.beans.ConstructorProperties;

public class UserTournamentStatsModel {
    public static final String TOTAL_TOURNAMENT = "total_tournament";
    public static final String WON_TOURNAMENT = "won_tournament";
    public static final String TOTAL_GAMES = "total_games";
    public static final String WON_GAMES = "won_games";
    public static final String MAX_LEVEL = "max_level";
    public static final String FUID = "fuid";
    public static final String CREATE_DATE = "create_date";
    public static final String UPDATE_DATE = "update_date";

    private int total_tournament;
    private int won_tournament;
    private int total_games;
    private int won_games;
    private int max_level;
    private int game_winning_percent;
    private String fuid;
    private long create_date;
    private long update_date;

    @ConstructorProperties({"total_tournament", "won_tournament", "total_games", "won_games", "max_level", "fuid", "create_date", "update_date"})
    public UserTournamentStatsModel(int total_tournament, int won_tournament, int total_games, int won_games, int max_level, String fuid, long create_date, long update_date) {
        this.total_tournament = total_tournament;
        this.won_tournament = won_tournament;
        this.total_games = total_games;
        this.won_games = won_games;
        this.max_level = max_level;
        this.fuid = fuid;
        this.create_date = create_date;
        this.update_date = update_date;
    }

    public UserTournamentStatsModel() {
    }

    public int getTotal_tournament() {
        return total_tournament;
    }

    public void setTotal_tournament(int total_tournament) {
        this.total_tournament = total_tournament;
    }

    public int getWon_tournament() {
        return won_tournament;
    }

    public void setWon_tournament(int won_tournament) {
        this.won_tournament = won_tournament;
    }

    public int getTotal_games() {
        return total_games;
    }

    public void setTotal_games(int total_games) {
        this.total_games = total_games;
    }

    public int getWon_games() {
        return won_games;
    }

    public void setWon_games(int won_games) {
        this.won_games = won_games;
    }

    public int getMax_level() {
        return max_level;
    }

    public void setMax_level(int max_level) {
        this.max_level = max_level;
    }

    public int getGame_winning_percent() {
        return game_winning_percent;
    }

    public void setGame_winning_percent(int game_winning_percent) {
        this.game_winning_percent = game_winning_percent;
    }

    public String getFuid() {
        return fuid;
    }

    public void setFuid(String fuid) {
        this.fuid = fuid;
    }

    public long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(long create_date) {
        this.create_date = create_date;
    }

    public long getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(long update_date) {
        this.update_date = update_date;
    }
}
