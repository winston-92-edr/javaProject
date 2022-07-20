package com.mynet.gameserver.enums;

//copy paste from canakokey server!!!
public enum CardType
{
    RED(0),
    BLACK(1),
    YELLOW(2),
    BLUE(3),
    COUNT(4);

    private int value;

    CardType(int i) {
        this.value  = i;
    }

    public int getValue(){
        return this.value;
    }

    private void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String[] arr = { "RED", "BLACK", "YELLOW", "BLUE" };
        return arr[getValue()];
    }
}

