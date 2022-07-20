package com.mynet.gameserver.logs.MoveFlat;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class CardFlat extends Table {
    public static CardFlat getRootAsCardFlat(ByteBuffer _bb) { return getRootAsCardFlat(_bb, new CardFlat()); }
    public static CardFlat getRootAsCardFlat(ByteBuffer _bb, CardFlat obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }

    public static int createCardFlat(FlatBufferBuilder builder,
                                     short number,
                                     short card_type,
                                     short bucket) {
        builder.startObject(3);
        CardFlat.addBucket(builder, bucket);
        CardFlat.addCardType(builder, card_type);
        CardFlat.addNumber(builder, number);
        return CardFlat.endCardFlat(builder);
    }

    public static void startCardFlat(FlatBufferBuilder builder) { builder.startObject(3); }

    public static void addNumber(FlatBufferBuilder builder, short number) { builder.addShort(0, number, 0); }

    public static void addCardType(FlatBufferBuilder builder, short cardType) { builder.addShort(1, cardType, 0); }

    public static void addBucket(FlatBufferBuilder builder, short bucket) { builder.addShort(2, bucket, 0); }

    public static int endCardFlat(FlatBufferBuilder builder) {
        int o = builder.endObject();
        return o;
    }

    public CardFlat __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
        return this;
    }

    public short number() {
        int o = __offset(4);
        return o != 0 ? bb.getShort(o + bb_pos) : 0;
    }

    public short cardType() {
        int o = __offset(6);
        return o != 0 ? bb.getShort(o + bb_pos) : 0;
    }

    public short bucket() {
        int o = __offset(8);
        return o != 0 ? bb.getShort(o + bb_pos) : 0;
    }
}
