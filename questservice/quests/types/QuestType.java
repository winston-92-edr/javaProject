package com.mynet.questservice.quests.types;

import com.mynet.questservice.quests.category.QuestCategory;

import java.util.LinkedHashMap;
import java.util.Map;

public enum QuestType {
    FINISH_GAME(0, QuestCategory.END_GAME), //F
    WON_GAME(1, QuestCategory.WON_GAME), //F
    BREAK_POT(2, QuestCategory.WON_GAME), //F
    WON_CLASSIC_GAME(3, QuestCategory.WON_GAME), //F
    WON_SINGLE_GAME(4, QuestCategory.WON_GAME), //F
    WON_PAIRED_GAME(5, QuestCategory.WON_GAME), //F
    WON_GAME_WITH_DOUBLE(6, QuestCategory.WON_GAME), //F
    WON_GAME_WITH_OKEY(7, QuestCategory.WON_GAME),  //F
    WON_GAME_WITH_LONG_TILE(8, QuestCategory.WON_GAME),  //F
    EARN_MONEY(9, QuestCategory.MONEY), //F
    JOIN_DUEL_TOURNAMENT(10, QuestCategory.TOURNAMENT), //F
    LEVEL_AT_DUEL(11, QuestCategory.TOURNAMENT), //F
    GET_FRIEND(12, QuestCategory.SOCIAL), //F
    INVITE_USER(13, QuestCategory.SOCIAL), //F
    GO_DOUBLE(14, QuestCategory.GAME_ACTION), //F
    FINISH_GAME_FOR_BOT(15,QuestCategory.BOT), //F
    FINISH_SINGLE_GAME(16,QuestCategory.END_GAME), //F
    FINISH_PAIRED_GAME(17,QuestCategory.END_GAME),  //F
    EARN_MONEY_SINGLE_GAME(18,QuestCategory.MONEY), //F
    EARN_MONEY_PAIRED_GAME(19,QuestCategory.MONEY), //F
    FINISH_GAME_AT_ROOM(20,QuestCategory.END_GAME), //F
    JOIN_CLASSIC_TOURNAMENT(21, QuestCategory.TOURNAMENT), //F
    WINNING_STREAK(22,QuestCategory.STREAK), //F
    SEND_MESSAGE_TO_TABLE(23,QuestCategory.SOCIAL), //F
    WON_GAME_WITH_FAKE_OKEY(24,QuestCategory.WON_GAME), //F
    LEVEL_AT_CLASSIC(25, QuestCategory.TOURNAMENT),
    DIFFERENT_DAYS_PLAY(26,QuestCategory.DIFFERENT_DAYS),
    WON_SINGLE_GAME_GAIN_XP(27,QuestCategory.WON_GAME),
    WON_PAIRED_GAME_GAIN_XP(28,QuestCategory.WON_GAME),
    WON_TOURNAMENT_GAME_GAIN_XP(29,QuestCategory.TOURNAMENT),
    FINISH_CLASSIC_GAME(30,QuestCategory.END_GAME),
    EARN_MONEY_CLASSIC(31,QuestCategory.MONEY),
    DIFFERENT_DAYS_LOGIN(32,QuestCategory.DIFFERENT_DAYS),
    LOST_SINGLE_GAME_GAIN_XP(33,QuestCategory.LOST_GAME),
    LOST_PAIRED_GAME_GAIN_XP(34,QuestCategory.LOST_GAME);

    private final int value;
    private final QuestCategory category;

    QuestType(int value, QuestCategory category) {
        this.value = value;
        this.category = category;
    }

    public int getValue() {
        return value;
    }

    private static final Map<Integer, QuestType> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (QuestType rae : QuestType.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static QuestType forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
