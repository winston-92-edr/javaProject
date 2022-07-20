package com.mynet.gameserver.logs.MoveFlat;


import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class FirstHandFlat extends Table {
    public static FirstHandFlat getRootAsFirstHandFlat(ByteBuffer _bb) { return getRootAsFirstHandFlat(_bb, new FirstHandFlat()); }
    public static FirstHandFlat getRootAsFirstHandFlat(ByteBuffer _bb, FirstHandFlat obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }

    public static int createFirstHandFlat(FlatBufferBuilder builder,
                                          int playersOffset,
                                          int cardsOffset) {
        builder.startObject(2);
        FirstHandFlat.addCards(builder, cardsOffset);
        FirstHandFlat.addPlayers(builder, playersOffset);
        return FirstHandFlat.endFirstHandFlat(builder);
    }

    public static void startFirstHandFlat(FlatBufferBuilder builder) { builder.startObject(2); }

    public static void addPlayers(FlatBufferBuilder builder, int playersOffset) {
        builder.addOffset(0, playersOffset, 0);
    }

    public static int createPlayersVector(FlatBufferBuilder builder, long[] data) {
        builder.startVector(8, data.length, 8);
        for (int i = data.length - 1; i >= 0; i--) builder.addLong(data[i]);
        return builder.endVector();
    }

    public static void startPlayersVector(FlatBufferBuilder builder, int numElems) {
        builder.startVector(8, numElems, 8);
    }

    public static void addCards(FlatBufferBuilder builder, int cardsOffset) { builder.addOffset(1, cardsOffset, 0); }

    public static int createCardsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }

    public static void startCardsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }

    public static int endFirstHandFlat(FlatBufferBuilder builder) {
        int o = builder.endObject();
        return o;
    }

    public FirstHandFlat __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
        return this;
    }

    public long players(int j) {
        int o = __offset(4);
        return o != 0 ? bb.getLong(__vector(o) + j * 8) : 0;
    }

    public int playersLength() {
        int o = __offset(4);
        return o != 0 ? __vector_len(o) : 0;
    }

    public ByteBuffer playersAsByteBuffer() {
        return __vector_as_bytebuffer(4, 8);
    }

    public CardFlat cards(int j) {
        return cards(new CardFlat(), j);
    }

    public CardFlat cards(CardFlat obj, int j) {
        int o = __offset(6);
        return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null;
    }

    public int cardsLength() {
        int o = __offset(6);
        return o != 0 ? __vector_len(o) : 0;
    }
}
