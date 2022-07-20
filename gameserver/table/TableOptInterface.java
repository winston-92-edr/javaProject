package com.mynet.gameserver.table;

public interface TableOptInterface
{

    void setProperty(int val);

    int getProperty();

    void setGameByPoint(boolean val);

    boolean getGameByPoint();

    void setSilentAudience(boolean val);

    boolean getSilentAudience();

    void setPerli(boolean val);

    boolean getPerli();

    void setCiftAcmaLimit(int val);

    int getCiftAcmaLimit();

    String OptionsString();
}

