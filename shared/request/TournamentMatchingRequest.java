package com.mynet.shared.request;

public class TournamentMatchingRequest {
    private int tournamentId = 1;

    public TournamentMatchingRequest(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public int getTournamentId() {
        return tournamentId;
    }
}
