package com.mynet.shared.response;

import com.mynet.shared.model.TournamentModel;
import com.mynet.shared.model.UserTournamentModel;

public class ClientTournamentInfoResponse {
    private TournamentModel tournament;
    private UserTournamentModel userLevel;
    private boolean active;
    private long date;
    private int type;

    public ClientTournamentInfoResponse(TournamentModel tournament) {
        this.tournament = tournament;
    }

    public void setUserLevel(UserTournamentModel userLevel) {
        this.userLevel = userLevel;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setType(int type) {
        this.type = type;
    }
}
