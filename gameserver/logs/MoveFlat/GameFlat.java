package com.mynet.gameserver.logs.MoveFlat;


import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class GameFlat extends Table {
    public static GameFlat getRootAsGameFlat(ByteBuffer _bb) { return getRootAsGameFlat(_bb, new GameFlat()); }
    public static GameFlat getRootAsGameFlat(ByteBuffer _bb, GameFlat obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
    public static boolean GameFlatBufferHasIdentifier(ByteBuffer _bb) { return __has_identifier(_bb, "GAME"); }

    public static int createGameFlat(FlatBufferBuilder builder,
                                     short lobbyId,
                                     short roomId,
                                     short tableId,
                                     long gameId,
                                     int okeyOffset,
                                     int firstHandsOffset,
                                     int movesOffset) {
        builder.startObject(7);
        GameFlat.addGameId(builder, gameId);
        GameFlat.addMoves(builder, movesOffset);
        GameFlat.addFirstHands(builder, firstHandsOffset);
        GameFlat.addOkey(builder, okeyOffset);
        GameFlat.addTableId(builder, tableId);
        GameFlat.addRoomId(builder, roomId);
        GameFlat.addLobbyId(builder, lobbyId);
        return GameFlat.endGameFlat(builder);
    }

    public static void startGameFlat(FlatBufferBuilder builder) { builder.startObject(7); }

    public static void addLobbyId(FlatBufferBuilder builder, short lobbyId) { builder.addShort(0, lobbyId, 0); }

    public static void addRoomId(FlatBufferBuilder builder, short roomId) { builder.addShort(1, roomId, 0); }

    public static void addTableId(FlatBufferBuilder builder, short tableId) { builder.addShort(2, tableId, 0); }

    public static void addGameId(FlatBufferBuilder builder, long gameId) { builder.addLong(3, gameId, 0); }

    public static void addOkey(FlatBufferBuilder builder, int okeyOffset) { builder.addOffset(4, okeyOffset, 0); }

    public static void addFirstHands(FlatBufferBuilder builder, int firstHandsOffset) { builder.addOffset(5, firstHandsOffset, 0); }

    public static int createFirstHandsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }

    public static void startFirstHandsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }

    public static void addMoves(FlatBufferBuilder builder, int movesOffset) { builder.addOffset(6, movesOffset, 0); }

    public static int createMovesVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }

    public static void startMovesVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }

    public static int endGameFlat(FlatBufferBuilder builder) {
        int o = builder.endObject();
        return o;
    }

    public static void finishGameFlatBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset, "GAME"); }

    public GameFlat __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
        return this;
    }

    public short lobbyId() {
        int o = __offset(4);
        return o != 0 ? bb.getShort(o + bb_pos) : 0;
    }

    public short roomId() {
        int o = __offset(6);
        return o != 0 ? bb.getShort(o + bb_pos) : 0;
    }

    public short tableId() {
        int o = __offset(8);
        return o != 0 ? bb.getShort(o + bb_pos) : 0;
    }

    public long gameId() {
        int o = __offset(10);
        return o != 0 ? bb.getLong(o + bb_pos) : 0;
    }

    public CardFlat okey() {
        return okey(new CardFlat());
    }

    public CardFlat okey(CardFlat obj) {
        int o = __offset(12);
        return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null;
    }

    public FirstHandFlat firstHands(int j) {
        return firstHands(new FirstHandFlat(), j);
    }

    public FirstHandFlat firstHands(FirstHandFlat obj, int j) {
        int o = __offset(14);
        return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null;
    }

    public int firstHandsLength() {
        int o = __offset(14);
        return o != 0 ? __vector_len(o) : 0;
    }

    public MoveFlat moves(int j) {
        return moves(new MoveFlat(), j);
    }

    public MoveFlat moves(MoveFlat obj, int j) {
        int o = __offset(16);
        return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null;
    }

    public int movesLength() {
        int o = __offset(16);
        return o != 0 ? __vector_len(o) : 0;
    }
}