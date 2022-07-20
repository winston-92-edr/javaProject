package com.mynet.socialserver.model;

import java.beans.ConstructorProperties;

public class UserTournamentStats {
    public static final String TOTAL_TOURNAMENT = "total_tournament";
    public static final String WON_TOURNAMENT = "won_tournament";
    public static final String TOTAL_GAMES = "total_games";
    public static final String WON_GAMES = "won_games";
    public static final String MAX_LEVEL = "max_level";
    public static final String FUID = "fuid";
    public static final String CREATE_DATE = "create_date";
    public static final String UPDATE_DATE = "update_date";

    public int total_tournament;
    public int won_tournament;
    public int total_games;
    public int won_games;
    public int max_level;
    public int game_winning_percent;
    public String fuid;
    public long create_date;
    public long update_date;

    @ConstructorProperties({"total_tournament", "won_tournament", "total_game" , "won_game", "max_level", "fuid", "create_date", "update_date"})
    public UserTournamentStats(int total_tournament, int won_tournament, int total_games, int won_games, int max_level, String fuid, long create_date, long update_date) {
        this.total_tournament = total_tournament;
        this.won_tournament = won_tournament;
        this.total_games = total_games;
        this.won_games = won_games;
        this.max_level = max_level;
        this.fuid = fuid;
        this.create_date = create_date;
        this.update_date = update_date;
    }

    public UserTournamentStats(int total_tournament, int won_tournament, int total_games, int won_games, int max_level, int game_winning_percent, String fuid, long create_date, long update_date) {
        this.total_tournament = total_tournament;
        this.won_tournament = won_tournament;
        this.total_games = total_games;
        this.won_games = won_games;
        this.max_level = max_level;
        this.game_winning_percent = game_winning_percent;
        this.fuid = fuid;
        this.create_date = create_date;
        this.update_date = update_date;
    }

    public static class Builder {
        public int total_tournament = 0;
        public int won_tournament = 0;
        public int total_games = 0;
        public int won_games = 0;
        public int max_level = 1;
        public int game_winning_percent = 0;
        public String fuid;
        public long create_date;
        public long update_date;

        public Builder(String fuid) {
            this.fuid = fuid;
        }

        public Builder setTotalTournament(int total_tournament) {
            this.total_tournament = total_tournament;
            return this;
        }

        public Builder setWonTournament(int won_tournament) {
            this.won_tournament = won_tournament;
            return this;
        }

        public Builder setTotalGames(int total_games) {
            this.total_games = total_games;
            return this;
        }

        public Builder setWonGames(int won_games) {
            this.won_games = won_games;
            return this;
        }

        public Builder setMaxLevel(int max_level) {
            this.max_level = max_level;
            return this;
        }

        public Builder setGameWinningPercent(int game_winning_percent) {
            this.game_winning_percent = game_winning_percent;
            return this;
        }

        public Builder setCreateDate(long create_date){
            this.create_date = create_date;
            return this;
        }

        public Builder setUpdateDate(long update_date){
            this.update_date = update_date;
            return this;
        }

        public UserTournamentStats build() {
            return new UserTournamentStats(total_tournament, won_tournament, total_games, won_games, max_level, game_winning_percent, fuid, create_date, update_date);
        }
    }

    public static UserTournamentStats create(int total_tournament, int won_tournament, int total_games, int won_games, int max_level, int game_winning_percent, String fuid, long create_date, long update_date) {
        return new Builder(fuid)
                .setTotalTournament(total_tournament)
                .setWonTournament(won_tournament)
                .setTotalGames(total_games)
                .setWonGames(won_games)
                .setMaxLevel(max_level)
                .setGameWinningPercent(game_winning_percent)
                .setCreateDate(create_date)
                .setUpdateDate(update_date)
                .build();
    }
}