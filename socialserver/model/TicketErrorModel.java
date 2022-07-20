package com.mynet.socialserver.model;

public class TicketErrorModel {
    String tournamentTitle;
    int cost;

    public TicketErrorModel(String tournamentTitle, int cost) {
        this.tournamentTitle = tournamentTitle;
        this.cost = cost;
    }
}
