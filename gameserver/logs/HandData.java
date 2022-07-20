package com.mynet.gameserver.logs;

import com.mynet.gameserver.okey.OkeyCard;

import java.util.ArrayList;
import java.util.List;

public class HandData {
    private OkeyCard[] cards;
    private List<Long> players;

    public OkeyCard[] getCards() {
        return cards;
    }

    public void setCards(OkeyCard[] cards) {
        this.cards = cards;
    }

    public List<Long> getPlayers() {
        return players;
    }

    public void setPlayers(List<Long> players) {
        this.players = players;
    }

    public void addPlayer(long fuid) {
        if (players == null) {
            players = new ArrayList<Long>();
        }
        if (!players.contains(fuid)) {
            players.add(fuid);
        }
    }
}
