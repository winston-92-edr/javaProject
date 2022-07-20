package com.mynet.shared.types;

/**
 * Created by ekaya on 18.02.2016.
 */
public enum DataSourceType
{
    OYUNLAR(0),
    WINNER_LOGS(1),
    LOGS(2),
    NUM_OF_TYPES(3);

    private final int cNumber;

    private DataSourceType(int cNumber)
    {
        this.cNumber = cNumber;
    }

    public int getValue()
    {
        return this.cNumber;
    }
}
