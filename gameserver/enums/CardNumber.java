package com.mynet.gameserver.enums;

//copy paste from canakokey server!!!
public enum CardNumber
{
    INVALID(-1),
    BIRLI(1),
    IKILI(2),
    UCLU(3),
    DORTLU(4),
    BESLI(5),
    ALTILI(6),
    YEDILI(7),
    SEKIZLI(8),
    DOKUZLU(9),
    ONLU(10),
    BACAK(11),
    KIZ(12),
    PAPAZ(13),
    OKEYALTI(14);

    private final int cNumber;

    private CardNumber(int cNumber)
    {
        this.cNumber = cNumber;
    }

    public int getValue()
    {
        return this.cNumber;
    }
}
