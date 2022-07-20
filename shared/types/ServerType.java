package com.mynet.shared.types;

import java.util.LinkedHashMap;
import java.util.Map;

public enum ServerType {
    GENERIC(0),
    DUELLO(1),
    TOURNAMENT(2),
    NONE(3);

    private final int value;

    ServerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    private static final Map<Integer, ServerType> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (ServerType rae : ServerType.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static ServerType forCode(int value) {
        return BY_CODE_MAP.get(value);
    }

    public String getLogKeyMoneyForRound() {
        switch (getValue()) {
            case 2:
                return "TOURNAMENT_MONEY_FOR_ROUND";
            case 1:
                return "FM_MoneyForRound";
            default:
                return "MoneyForRound";
        }
    }

    public String getLogKeyMoneyForPot() {
        switch (getValue()) {
            case 2:
                return "TOURNAMENT_MONEY_FOR_POT";
            case 1:
                return "FM_MoneyForPot";
            default:
                return "MoneyForPot";
        }
    }


    @Override
    public String toString() {
        switch (getValue()) {
            case 2:
                return "TOURNAMENT";
            case 1:
                return "FASTMODE";
            default:
                return "DEFAULT";
        }
    }

    public String getLogKeyBreakPot() {
        switch (getValue()) {
            case 2:
                return "TOURNAMENT_BREAK_POT";
            case 1:
                return "FM_BreakPot";
            default:
                return "BreakPot";
        }
    }

    public String getLogKeyPayCutPot() {
        switch (getValue()) {
            case 2:
                return "TOURNAMENT_PAYCUT_POT";
            case 1:
                return "FM_PayCutPot";
            default:
                return "PayCutPot";
        }
    }

    public String getLogKeyBreakPotEsli() {
        switch (getValue()) {
            case 2:
                return "TOURNAMENT_BREAK_POT_ESLI";
            case 1:
                return "FM_BreakPotEsli";
            default:
                return "BreakPotEsli";
        }
    }

    public String getLogKeyWinnerMoney() {
        switch (getValue()) {
            case 2:
                return "TOURNAMENT_WINNING_MONEY";
            case 1:
                return "FM_WinningMoney";
            default:
                return "WinningMoney";
        }
    }

    public String getLogKeyPayCutWinningMoney() {
        switch (getValue()) {
            case 2:
                return "TOURNAMENT_PAYCUT_WINNING_MONEY";
            case 1:
                return "FM_PayCutWinningMoney";
            default:
                return "PayCutWinningMoney";
        }
    }

    public String getLogKeyPayCutWinningMoneyEsli() {
        switch (getValue()) {
            case 2:
                return "TOURNAMENT_PAYCUT_WINNING_MONEY_ESLI";
            case 1:
                return "FM_PayCutWinningMoneyEsli";
            default:
                return "PayCutWinningMoneyEsli";
        }
    }

    public String getLogKeyWinningOkeyAltiMoney() {
        switch (getValue()) {
            case 2:
                return "TOURNAMENT_GET_WINNING_OKEY_ALTI_MONEY";
            case 1:
                return "FM_getWinningOkeyAltiMoney";
            default:
                return "getWinningOkeyAltiMoney";
        }
    }

    public String getLogDrawGameMoney() {
        return "DRAW_GAME_MONEY";
    }
}
