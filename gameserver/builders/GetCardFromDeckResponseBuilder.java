package com.mynet.gameserver.builders;

import com.mynet.gameserver.response.GetCardFromDeckResponse;

public class GetCardFromDeckResponseBuilder {
    private long tableId;
    private int side;
    private String cardId;
    private boolean fromDeck;
    private int remainedCount;
    private boolean autoplay;

    public GetCardFromDeckResponseBuilder setTableId(long tableId) {
        this.tableId = tableId;
        return this;
    }

    public GetCardFromDeckResponseBuilder setSide(int side) {
        this.side = side;
        return this;
    }

    public GetCardFromDeckResponseBuilder setCardId(String cardId) {
        this.cardId = cardId;
        return this;
    }

    public GetCardFromDeckResponseBuilder setFromDeck(boolean fromDeck) {
        this.fromDeck = fromDeck;
        return this;
    }

    public GetCardFromDeckResponseBuilder setRemainedCount(int remainedCount) {
        this.remainedCount = remainedCount;
        return this;
    }

    public GetCardFromDeckResponseBuilder setAutoplay(boolean autoplay) {
        this.autoplay = autoplay;
        return this;
    }

    public GetCardFromDeckResponse createGetCardFromDeckResponse() {
        return new GetCardFromDeckResponse(tableId, side, cardId, fromDeck, remainedCount, autoplay);
    }
}