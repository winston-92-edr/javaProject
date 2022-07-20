package com.mynet.gameserver.response;

import com.mynet.gameserver.model.ThrownCardsModel;

import java.util.ArrayList;

public class ThrownCardsResponse {
    ArrayList<ThrownCardsModel> thrownCards;
    int sideCount;

    public ThrownCardsResponse(ArrayList<ThrownCardsModel> thrownCards, int sideCount) {
        this.thrownCards = thrownCards;
        this.sideCount = sideCount;
    }

    @Override
    public String toString() {
        return "ThrownCardsResponse{" +
                "thrownCards=" + thrownCards +
                '}';
    }
}
