package com.mynet.gameserver.logs.MoveFlat;


import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class MoveFlat extends Table {
    public static MoveFlat getRootAsMoveFlat(ByteBuffer _bb) { return getRootAsMoveFlat(_bb, new MoveFlat()); }
    public static MoveFlat getRootAsMoveFlat(ByteBuffer _bb, MoveFlat obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }

    public static int createMoveFlat(FlatBufferBuilder builder,
                                     long from,
                                     long to,
                                     short fromSide,
                                     short toSide,
                                     byte moveDirection,
                                     byte moveDestination,
                                     boolean autoPlay,
                                     int cardOffset) {
        builder.startObject(8);
        MoveFlat.addTo(builder, to);
        MoveFlat.addFrom(builder, from);
        MoveFlat.addCard(builder, cardOffset);
        MoveFlat.addToSide(builder, toSide);
        MoveFlat.addFromSide(builder, fromSide);
        MoveFlat.addAutoPlay(builder, autoPlay);
        MoveFlat.addMoveDestination(builder, moveDestination);
        MoveFlat.addMoveDirection(builder, moveDirection);
        return MoveFlat.endMoveFlat(builder);
    }

    public static void startMoveFlat(FlatBufferBuilder builder) {
        builder.startObject(8);
    }

    public static void addFrom(FlatBufferBuilder builder, long from) { builder.addLong(0, from, 0); }

    public static void addTo(FlatBufferBuilder builder, long to) { builder.addLong(1, to, 0); }

    public static void addFromSide(FlatBufferBuilder builder, short fromSide) {
        builder.addShort(2, fromSide, 0);
    }

    public static void addToSide(FlatBufferBuilder builder, short toSide) {
        builder.addShort(3, toSide, 0);
    }

    public static void addMoveDirection(FlatBufferBuilder builder, byte moveDirection) {
        builder.addByte(4, moveDirection, 0);
    }

    public static void addMoveDestination(FlatBufferBuilder builder, byte moveDestination) {
        builder.addByte(5, moveDestination, 0);
    }

    public static void addAutoPlay(FlatBufferBuilder builder, boolean autoPlay) {
        builder.addBoolean(6, autoPlay, false);
    }

    public static void addCard(FlatBufferBuilder builder, int cardOffset) {
        builder.addOffset(7, cardOffset, 0);
    }

    public static int endMoveFlat(FlatBufferBuilder builder) {
        int o = builder.endObject();
        return o;
    }

    public MoveFlat __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
        return this;
    }

    public long from() {
        int o = __offset(4);
        return o != 0 ? bb.getLong(o + bb_pos) : 0;
    }

    public long to() {
        int o = __offset(6);
        return o != 0 ? bb.getLong(o + bb_pos) : 0;
    }

    public short fromSide() {
        int o = __offset(8);
        return o != 0 ? bb.getShort(o + bb_pos) : 0;
    }

    public short toSide() {
        int o = __offset(10);
        return o != 0 ? bb.getShort(o + bb_pos) : 0;
    }

    public byte moveDirection() {
        int o = __offset(12);
        return o != 0 ? bb.get(o + bb_pos) : 0;
    }

    public byte moveDestination() {
        int o = __offset(14);
        return o != 0 ? bb.get(o + bb_pos) : 0;
    }

    public boolean autoPlay() {
        int o = __offset(16);
        return o != 0 && 0 != bb.get(o + bb_pos);
    }

    public CardFlat card() {
        return card(new CardFlat());
    }

    public CardFlat card(CardFlat obj) {
        int o = __offset(18);
        return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null;
    }
}
