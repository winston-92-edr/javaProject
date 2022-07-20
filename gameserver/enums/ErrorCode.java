package com.mynet.gameserver.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum ErrorCode {
    LESS_THAN_14(1), //cardSizeLessThen14
    NOT_IN_USER_HAND(2), //thisCardIsNotInUSerHand
    DOESNT_MATCH_SERVER(3), //lastCardDoesntMatchServer
    DUPLICATE_CARD(4), //duplicatedCard
    LAST_CARD_NULL(5), //myShouldBeLastCardNULL
    GROUP_LESS_THAN_3(6), //thisCardGoupLessThen3
    NOT_SAME_COLOR(7),  //thisCardNotSameColor
    NOT_IN_PER(8),  // thisCardNotInPer
    PER_LESS_THAN_3(9), //perLessThen3
    NOT_IN_SAME_COLOR_PER(10), //thisCardNotInSameColorPer
    NOT_YOUR_TURN(11), //isNotYourTurn
    OKEY_MORE_THAN_2(12), //okeyMoreThen2
    LESS_THAN_7_DOUBLE(13),
    MORE_THAN_14(14), //doubleSizeLessThen7
    NOT_VIP_TABLE(15),
    NOT_VIP_ROOM(16),
    NOT_VIP_OPEN_TABLE(17),
    LOW_BET_TABLE(18),
    LOW_BET_ROOM(19),
    LOW_BET_OPEN_TABLE(20),
    NOT_ENOUGH_MONEY_TABLE(21),
    NOT_ENOUGH_MONEY_ROOM(22),
    NOT_ENOUGH_MONEY_OPEN_TABLE(23),
    CANNOT_AUDIENCE(24),
    ALREADY_HAVE_TABLE(25),
    ROOM_NULL(26),
    TABLE_NULL(27),
    CANNOT_OPEN_TABLE(28),
    //LEFT_A_USER(29),
    NOT_YOUR_TABLE(30),
    NOT_AVAILABLE_TO_SIT(31),
    USER_ACTION_NOT_IN_USER_HAND(32),
    GET_CARD_NOT_YOUR_TURN(33),
    USER_ACTION_NOT_YOUR_TURN(34),
    USER_ACTION_LESS_THAN_14(35),
    TOURNAMENT_NOT_AVAILABLE(36),
    GENERAL_ERROR(37),
    TOURNAMENT_NOT_ACTIVE(38),
    //USER_HAS_ALREADY_TABLE(39),
    MAINTENANCE_MODE(40),
    USER_HAS_ROOM_ALREADY(41),
    WRONG_ROOM_ID(42),
    USER_ALREADY_IN_TOURNAMENT(43),
    NOT_ENOUGH_TICKET(44),
    SAME_IP(45),
    INVALID_LOGIN(46),
    GIFT_ERROR(47),
    PRIVATE_CHAT_NOT_VIP(48),
    MUTED(49),
    AUDIENCE_LIMIT(50),
    FINISHING_HAND_NULL(51),
    ROOM_LIMIT(52),
    NO_AVAILABLE_TABLE(53),
    BANNED(54),
    PRIVATE_CHAT_NOT_ONLINE(55),
    NODE_NULL(56),
    ROBOT_NOT_AVAILABLE_TO_SIT(57),
    NOT_ENOUGH_MONEY_QUICK_PLAY(58),
    LOW_BET_QUICK_PLAY(59);

    private final int value;

    private ErrorCode(int value) {
        this.value = value;
    }

    private static final Map<Integer, ErrorCode> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (ErrorCode rae : ErrorCode.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static ErrorCode forCode(int value) {
        return BY_CODE_MAP.get(value);
    }

    public int getValue() {
        return value;
    }
}
