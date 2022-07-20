package com.mynet.shared.model;

public class TournamentBadge {
    public static final String TOURNAMENT_ID = "tournament_id";
    public static final String BADGE_ID = "badge_id";

    private int tournamentId;
    private int badgeId;

    public TournamentBadge(int tournamentId, int badgeId) {
        this.tournamentId = tournamentId;
        this.badgeId = badgeId;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public int getBadgeId() {
        return badgeId;
    }

    @Override
    public String toString() {
        return "badge_" + tournamentId + "_" + badgeId;
    }
}
