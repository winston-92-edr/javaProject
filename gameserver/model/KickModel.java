package com.mynet.gameserver.model;

import com.mynet.gameserver.enums.GameEndStatus;
import com.mynet.gameserver.okey.Table;

import java.util.List;

public class KickModel {
    private final GameEndStatus gameEndStatus;
    private final String winnerFuid;
    private final String winnerName;
    private final boolean pot;
    private final boolean partner;
    private String partnerFuid;
    private String partnerName;
    private List<HandOverCardModel> finishedHand;
    private final long potValue;
    private final String okey;
    private final long bet;

    public KickModel(GameEndStatus gameEndStatus, String winnerFuid, String winnerName, boolean pot, boolean partner, List<HandOverCardModel> finishedHand, long potValue, String okey, long bet) {
        this.gameEndStatus = gameEndStatus;
        this.winnerFuid = winnerFuid;
        this.winnerName = winnerName;
        this.pot = pot;
        this.partner = partner;
        this.finishedHand = finishedHand;
        this.potValue = potValue;
        this.okey = okey;
        this.bet = bet;
    }

    public void setPartnerFuid(String partnerFuid) {
        this.partnerFuid = partnerFuid;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }
}
