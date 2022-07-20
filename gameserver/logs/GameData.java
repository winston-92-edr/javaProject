package com.mynet.gameserver.logs;

import com.google.flatbuffers.FlatBufferBuilder;
import com.mynet.gameserver.logs.MoveFlat.*;
import com.mynet.gameserver.logs.move.Move;
import com.mynet.gameserver.logs.move.MoveDestination;
import com.mynet.gameserver.logs.move.MoveDirection;
import com.mynet.gameserver.okey.OkeyCard;
import com.mynet.shared.logs.QueueElement;
import org.apache.log4j.Logger;

import java.util.List;

public class GameData extends QueueElement {
    final static Logger logger = Logger.getLogger(GameData.class);

    private byte[] bytes;

    public GameData(List<Move> moveList, HandData[] firstHands, OkeyCard okey) {
        this.init(moveList, firstHands, okey);
    }

    private void init(List<Move> moveList, HandData[] firstHands, OkeyCard okey) {
        try {
            FlatBufferBuilder builder = new FlatBufferBuilder(0);
            int size = moveList.size();
            int[] moveFlatList = new int[size];

            CardFlat.startCardFlat(builder);

            if(okey == null) return;

            CardFlat.addNumber(builder, (short)okey.getCardNumber());
            CardFlat.addCardType(builder, (short)okey.getCardType());
            CardFlat.addBucket(builder, (short)okey.getBucket());
            int okeyCardFlat = CardFlat.endCardFlat(builder);

            long gameId = -1;
            int lobbyId = -1;
            int roomId = -1;
            long tableId = -1;

            for (int i = size - 1; i >= 0; i--) {
                Move move = moveList.get(i);

                if (gameId == -1) {
                    gameId = move.getGameId();
                    lobbyId = move.getLobbyId();
                    roomId = move.getRoomId();
                    tableId = move.getTableId();
                }

                CardFlat.startCardFlat(builder);
                CardFlat.addNumber(builder, (short)move.getNumber());
                CardFlat.addCardType(builder, (short)move.getType());
                CardFlat.addBucket(builder, (short)move.getBucket());
                int cardFlat = CardFlat.endCardFlat(builder);

                MoveFlat.startMoveFlat(builder);
                MoveFlat.addFrom(builder, move.getFrom());
                MoveFlat.addFromSide(builder, move.getFromSide());
                MoveFlat.addTo(builder, move.getTo());
                MoveFlat.addToSide(builder, move.getToSide());
                MoveFlat.addMoveDirection(builder, getDirection(move.getDirection()));
                MoveFlat.addMoveDestination(builder, getDestination(move.getDestination()));
                MoveFlat.addAutoPlay(builder, move.isAutoPlay());
                MoveFlat.addCard(builder, cardFlat);
                int moveFlat = MoveFlat.endMoveFlat(builder);
                moveFlatList[i] = moveFlat;
            }

            int[] firstHandsFlat = new int[firstHands.length];
            int index = 0;
            for (HandData handData : firstHands) {

                List<Long> players = handData.getPlayers();
                if(players != null){
                    long[] playersFlat = new long[players.size()];
                    for (int i = players.size() - 1; i >= 0; i--) {
                        playersFlat[i] = players.get(i);
                    }
                    int playersVector = FirstHandFlat.createPlayersVector(builder, playersFlat);
                    OkeyCard[] cards = handData.getCards();

                    int[] cardsFlat = new int[cards.length];
                    for (int j = cards.length - 1; j >= 0; j--) {
                        OkeyCard card = cards[j];
                        CardFlat.startCardFlat(builder);
                        CardFlat.addNumber(builder, (short) card.getCardNumber());
                        CardFlat.addCardType(builder, (short) card.getCardType());
                        CardFlat.addBucket(builder, (short) card.getBucket());
                        cardsFlat[j] = CardFlat.endCardFlat(builder);
                    }

                    int cardsVector = FirstHandFlat.createCardsVector(builder, cardsFlat);

                    FirstHandFlat.startFirstHandFlat(builder);
                    FirstHandFlat.addPlayers(builder, playersVector);
                    FirstHandFlat.addCards(builder, cardsVector);
                    firstHandsFlat[index++] = FirstHandFlat.endFirstHandFlat(builder);
                }

            }

            int movesVector = GameFlat.createMovesVector(builder, moveFlatList);
            int firstHandsVector = GameFlat.createFirstHandsVector(builder, firstHandsFlat);

            GameFlat.startGameFlat(builder);
            GameFlat.addGameId(builder, gameId);
            GameFlat.addLobbyId(builder, (short)lobbyId);
            GameFlat.addRoomId(builder, (short)roomId);
            GameFlat.addTableId(builder, (short)tableId);
            GameFlat.addOkey(builder, okeyCardFlat);
            GameFlat.addFirstHands(builder, firstHandsVector);
            GameFlat.addMoves(builder, movesVector);
            int gameFlat = GameFlat.endGameFlat(builder);
            builder.finish(gameFlat);

            bytes = builder.sizedByteArray();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private byte getDestination(MoveDestination destination) {
        switch (destination) {
            case DECK:
                return Destination.DECK;
            case SIDE:
                return Destination.SIDE;
            case PLAYER:
                return Destination.PLAYER;
        }

        return (byte)-1;
    }

    private byte getDirection(MoveDirection direction) {
        switch (direction) {
            case IN:
                return Direction.IN;
            case OUT:
                return Direction.OUT;
        }
        return (byte)-1;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}
