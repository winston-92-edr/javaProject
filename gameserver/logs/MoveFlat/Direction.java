package com.mynet.gameserver.logs.MoveFlat;

public final class Direction {
    private Direction() { }
    public static final byte IN = 0;
    public static final byte OUT = 1;

    public static final String[] names = { "IN", "OUT", };

    public static String name(int e) { return names[e]; }
}

