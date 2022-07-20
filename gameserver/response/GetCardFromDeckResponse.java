package com.mynet.gameserver.response;

public class GetCardFromDeckResponse {
    private long tableId;
    private int side;
    private String cardId;
    private boolean fromDeck;
    private int remainingCardsCount;
    private boolean autoplay;

    public GetCardFromDeckResponse(long tableId, int side, String cardId, boolean fromDeck, int remainingCardsCount, boolean autoplay) {
        this.tableId = tableId;
        this.side = side;
        this.cardId = cardId;
        this.fromDeck = fromDeck;
        this.remainingCardsCount = remainingCardsCount;
        this.autoplay = autoplay;
    }
}
