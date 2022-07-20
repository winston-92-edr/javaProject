package com.mynet.shared.request;

public class JoinTournamentRequest {
    private int tournamentId;

    public int getTournamentId() {
        return tournamentId;
    }

    public JoinTournamentRequest(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public JoinTournamentRequest() {
    }
}
