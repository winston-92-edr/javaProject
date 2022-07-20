package com.mynet.gameserver.logs.MoveFlat;

public final class Destination {
    private Destination() { }
    public static final byte DECK = 0;
    public static final byte SIDE = 1;
    public static final byte PLAYER = 2;

    public static final String[] names = { "DECK", "SIDE", "PLAYER", };

    public static String name(int e) { return names[e]; }
}
