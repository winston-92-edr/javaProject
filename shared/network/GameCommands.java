package com.mynet.shared.network;

import com.mynet.shared.types.RequestType;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GameCommands {
    //INSIDE SERVER
    ADD_USER(20000, RequestType.SERVER),
    REMOVE_USER(20001, RequestType.SERVER),
    REGISTER_NODE(20002, RequestType.SERVER),
    SERVER_MESSAGE(20003, RequestType.GAME),
    MATCH(20004, RequestType.SERVER),
    CHANGE_GAME_NODE(20005, RequestType.SERVER),
    ADD_USER_TO_ROOM(20006, RequestType.SERVER),
    REMOVE_USER_FROM_ROOM(20007, RequestType.SERVER),
    SOCKET_DISCONNECT(67, RequestType.GAME),
    SERVER_USER_STATUS_UPDATED(1112, RequestType.SERVER),

    //OLD COMMANDS
    SIT_TABLE(8, RequestType.GAME),
    JOIN_AN_AUDIENCE(10, RequestType.TABLE),
    LEFT_AN_AUDIENCE(11, RequestType.GAME),
    USER_SIT_A_TABLE(17, RequestType.GAME),
    LEFT_A_GAMER(13, RequestType.GAME),
    GAME_ID(33, RequestType.GAME),
    INVALID_LOGIN(46, RequestType.GAME),
    HAND_OVER_ERROR(49, RequestType.GAME),
    QUICK_PLAY_2(92, RequestType.TABLE),
    SIT_TABLE_2(94, RequestType.TABLE),
    ENTER_ROOM_2(97, RequestType.GAME),
    INVITE_RESULT(98, RequestType.GAME),
    SEND_VIP_TABLE_MESSAGE(203, RequestType.GAME),
    GIFT_ERROR(407, RequestType.GAME),
    GO_BEHIND_FRIEND(710, RequestType.GAME),
    QUICK_PLAY(777, RequestType.TABLE),
    CANT_START_GAME(802, RequestType.SOCIAL),
    QUICK_PLAY_SERVER(807, RequestType.SERVER),
    SIT_TABLE_SERVER(808, RequestType.SERVER),
    JOIN_AN_AUDIENCE_SERVER(809, RequestType.SERVER),
    SEND_USER_NOT_ENOUGH_MESSAGE(1002, RequestType.GAME),
    GO_TO_FRIEND(1013, RequestType.SOCIAL),
    SAME_IP(1014, RequestType.GAME),
    LEVEL_UP(1111, RequestType.GAME),
    GAME_SERVER_ERROR(10000, RequestType.SOCIAL),

    //NEW COMMANDS
    FLY_MATCHMAKING(10004, RequestType.SOCIAL),
    SIT_TABLE_REQUEST(10005, RequestType.SERVER),
    TOURNAMENT_MATCHMAKING(10006, RequestType.MATCH),
    CLAIM_AWARD(10007, RequestType.SOCIAL),
    CHECK_USER_TOURNAMENT(10008, RequestType.SOCIAL),
    JOIN_TOURNAMENT(10009, RequestType.SOCIAL),

    GET_TOURNAMENT_PROFILE_DETAILS(10011, RequestType.SOCIAL),
    GET_NEW_USER_STEP_DETAILS(10012, RequestType.SOCIAL),
    GET_TOURNAMENTS_INFO(10013, RequestType.SOCIAL),
    CANCEL_MATCHMAKING(10014, RequestType.MATCH),
    GET_ROOM_TABLES(10015, RequestType.GAME),
    SEND_USER_LOW_BET(10016, RequestType.MATCH),
    ENTER_ROOM_FRIEND(10017, RequestType.GAME),
    REMOVE_TABLE(10018, RequestType.TABLE),
    UPDATE_TABLE(10019, RequestType.TABLE),
    QUEST_XP_UPDATED(10020, RequestType.QUEST),
    QUEST_USER_INFO(10021, RequestType.QUEST),
    QUEST_COMPLETED(10022, RequestType.QUEST),
    CHECK_QUEST(10023, RequestType.QUEST),
    AWARDS_UPDATED(10024, RequestType.QUEST),
    SEASON_INFO(10025, RequestType.QUEST),
    EXTRA_XP_AWARD(10026, RequestType.QUEST),
    FRIEND_REQUEST(10027, RequestType.SOCIAL),
    ACTIVE_FRIEND(10029, RequestType.SOCIAL),
    GO_TO_YOUR_FRIEND(10032, RequestType.SOCIAL),
    SIT_NODE_TABLE(10033, RequestType.GAME),
    JOIN_NODE_TABLE(10034, RequestType.GAME),
    ADD_TABLE(10035, RequestType.TABLE),
    EXTRA_TIME(10037, RequestType.GAME),

    ENTER_TABLE(20023, RequestType.TABLE),
    PROXY_ENTER_TABLE(20024, RequestType.SERVER),
    GAME_ENTER_TABLE(20025, RequestType.GAME),
    LEFT_TABLE(95, RequestType.GAME),
    LEFT_A_USER(20026, RequestType.GAME),
    LOGIN(1, RequestType.SERVER),
    GET_THROWN_CARDS(79, RequestType.GAME),
    TABLE_INFO(26, RequestType.GAME),
    TABLES_INFO(1034, RequestType.TABLE),
    ERROR(666, RequestType.GAME),
    SEND_HAND_DRAW(77, RequestType.GAME),
    OPEN_TABLE(93, RequestType.GAME),
    START_GAME(19, RequestType.GAME),
    SEND_USER_ACTION(20, RequestType.GAME),
    SEND_GO_DOUBLE(23, RequestType.GAME),
    GET_CARD_FROM_DECK(31, RequestType.GAME),
    SEND_HAND_OVER(32, RequestType.GAME),
    KICK_USER_FROM_TABLE(43, RequestType.GAME),
    GET_READY(47, RequestType.GAME),
    YOUR_HAND(99, RequestType.GAME),
    NOTIFY_FOR_AUDIENCE(58, RequestType.GAME),
    JOIN_AN_OPPONENT(9, RequestType.GAME),
    GET_ONLINE_FRIENDS(10031, RequestType.SOCIAL),
    UPDATE_FRIEND_STATUS(20027, RequestType.SOCIAL),
    SEND_INVITE_REQUEST(44, RequestType.GAME),
    CHECK_AVAILABLE_INVITE(20028, RequestType.SERVER),
    ADD_FRIEND(10028, RequestType.SOCIAL),
    REMOVE_FRIEND(10030, RequestType.SOCIAL),
    TABLE_CHAT(2, RequestType.CHAT),
    GET_USERS_IN_LOBBY(81, RequestType.GAME),
    USER_TICKET_UPDATED(10010, RequestType.SOCIAL),
    USER_RECEIVED_MONEY(555, RequestType.SOCIAL),
    VIP_STATUS_UPDATED(201, RequestType.SOCIAL),
    GET_PROFILE_DETAILS(307, RequestType.SOCIAL),
    SEND_GIFT(400, RequestType.GAME),
    RECEIVE_GIFT(401, RequestType.GAME),
    GET_GAME_STATUS(78, RequestType.GAME),
    SEND_USER_STATE(20008, RequestType.GAME),
    GET_ROOMS_USERS_COUNT(502, RequestType.TABLE),
    PRIVATE_CHAT(20029, RequestType.SOCIAL),
    CONNECT(20030, RequestType.SERVER),
    PRIVATE_CHAT_STATUS(20031, RequestType.SOCIAL),
    RESET_TABLE(1025, RequestType.GAME),
    PING(1015, RequestType.SERVER),
    SEND_TOURNAMENT_USER_STATE(20009, RequestType.GAME),
    JOINED_TO_STOPPED_GAME(28, RequestType.GAME),
    ENTER_ROOM(500, RequestType.GAME),
    LEAVE_ROOM(501, RequestType.GAME),
    USER_RECEIVED_MONEY_TABLE(556, RequestType.GAME),
    SUSPEND(1008, RequestType.GAME),
    AWAKE(1009, RequestType.GAME),
    SUSPEND_AWAKE(1010, RequestType.GAME),
    START_NEXT_ROUND(61, RequestType.GAME), //Belki tekrar ekleriz
    MUTE_USER(10036, RequestType.GAME),
    GET_USER_GIFTS(20032, RequestType.GAME),
    PURCHASE_NOTIFICATION(20033, RequestType.SOCIAL),
    PURCHASE_NOTIFICATION_RESULT(20034, RequestType.SOCIAL),
    RESET_TIMER(20035, RequestType.GAME),
    START_PRIVATE_CHAT(20036, RequestType.SOCIAL),
    RESTART_GAME(27, RequestType.GAME),
    UPDATE_USER_OPTIONS(601, RequestType.SOCIAL),
    INFO(20037, RequestType.GAME),
    CHANGE_USER_SERVER_TYPE(20038, RequestType.SERVER),
    CHAT_LEFT_TABLE(20039, RequestType.SERVER),
    CHAT_ENTER_TABLE(20040, RequestType.SERVER),
    UPDATE_FRIENDS(20041, RequestType.SOCIAL),
    SET_PROFANITY_FILTER(20042, RequestType.CHAT),
    GET_QUICK_PLAY_ROOMS(20043, RequestType.TABLE);

    private final int value;
    private final RequestType type;
    private final RequestType[] tags;

    GameCommands(int value, RequestType i) {
        this.value = value;
        this.type = i;
        this.tags = new RequestType[]{};
    }

    GameCommands(int value, RequestType i, RequestType[] tags) {
        this.value = value;
        this.type = i;
        this.tags = tags;
    }

    public int getValue() {
        return this.value;
    }

    public RequestType getType() {
        return type;
    }

    public RequestType[] getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    private static final Map<Integer, GameCommands> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GameCommands rae : GameCommands.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static GameCommands forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
