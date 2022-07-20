package com.mynet.gameserver.logs.move;

import com.mynet.shared.logs.QueueElement;

import java.nio.ByteBuffer;

public class Move extends QueueElement {
    // GAME INFO
    private long gameId;
    private long from;
    private long to;
    private int lobbyId;
    private int roomId;
    private int tableId;
    private short fromSide;
    private short toSide;

    // DIRECTION
    private MoveDirection direction;
    private MoveDestination destination;

    // CARD
    private int number;
    private int type;
    private int bucket;
    private boolean autoPlay;

    public Move() {
        direction = MoveDirection.IN;
        destination = MoveDestination.DECK;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {

        this.type = type;
    }

    public int getBucket() {
        return bucket;
    }

    public void setBucket(int bucket) {
        this.bucket = bucket;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public MoveDirection getDirection() {
        return direction;
    }

    public void setDirection(MoveDirection direction) {
        this.direction = direction;
    }

    public MoveDestination getDestination() {
        return destination;
    }

    public void setDestination(MoveDestination destination) {
        this.destination = destination;
    }

    public short getFromSide() {
        return fromSide;
    }

    public void setFromSide(short fromSide) {
        this.fromSide = fromSide;
    }

    public short getToSide() {
        return toSide;
    }

    public void setToSide(short toSide) {
        this.toSide = toSide;
    }

    @Override
    public String toString() {
        return "\tFrom " + from + "\n" +
                "\tFromSide " + fromSide + "\n" +
                "\tTo " + to + "\n" +
                "\tToSide " + toSide + "\n" +
                "\tDirection " + direction.name() + "\n" +
                "\tDestination " + destination.name() + "\n" +
                "\tAutoPlay " + autoPlay + "\n" +
                "\t\tCard Number " + number + "\n" +
                "\t\tCard Type " + type + "\n" +
                "\t\tCard Bucket " + bucket + "\n";
    }

    @Override
    public byte[] getBytes() {
        final int longSize = (Long.SIZE / 8);
        final int otherTypesSize = 1;

        byte[] bytes = new byte[(longSize * 3) + (otherTypesSize * 9)];

        int index = 0;
        index = createAndLocateBytesForLong(bytes, index, gameId);
        index = createAndLocateBytesForLong(bytes, index, from);
        index = createAndLocateBytesForLong(bytes, index, to);
        index = createAndLocateBytesForInteger(bytes, index, lobbyId);
        index = createAndLocateBytesForInteger(bytes, index, roomId);
        index = createAndLocateBytesForInteger(bytes, index, tableId);
        index = createAndLocateBytesForInteger(bytes, index, direction.getValue());
        index = createAndLocateBytesForInteger(bytes, index, destination.getValue());
        index = createAndLocateBytesForInteger(bytes, index, number);
        index = createAndLocateBytesForInteger(bytes, index, type);
        index = createAndLocateBytesForInteger(bytes, index, bucket);
        bytes[index] = (byte)(autoPlay ? 1 : 0);

        return bytes;
    }

    private int createAndLocateBytesForLong(byte[] bytes, int index, long value) {
        final int longSize = (Long.SIZE / 8);

        ByteBuffer buffer = ByteBuffer.allocate(longSize);
        buffer.putLong(value);
        byte[] array = buffer.array();

        for (int i = 0; i < array.length; i++, index++) {
            bytes[index] = array[i];
        }

        return index;
    }

    private int createAndLocateBytesForInteger(byte[] bytes, int index, long value) {
        final int intSize = (Integer.SIZE / 8);

        ByteBuffer buffer = ByteBuffer.allocate(intSize);
        buffer.putLong(value);
        byte[] array = buffer.array();
        bytes[index++] = array[array.length - 1];

        return index;
    }
}
