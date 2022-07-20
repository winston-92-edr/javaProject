package com.mynet.shared.network;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.mynet.bonusservice.model.type.ComparisonTypes;
import com.mynet.gameserver.enums.*;
import com.mynet.shared.enums.PlayerSide;
import com.mynet.shared.types.ClaimAwardType;
import com.mynet.shared.types.SettingsTypes;
import com.mynet.socialserver.enums.FriendStatus;
import com.mynet.tableservice.enums.TableFullnessFilter;
import com.mynet.tableservice.enums.TablePairedFilter;
import com.mynet.tableservice.enums.TableRobotFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.model.UserTournamentState;
import com.mynet.shared.response.UpdateTicketResponse;
import com.mynet.shared.types.ServerType;

import java.lang.reflect.Type;

public class NetworkMessage {
    private static final short SOCKET_PROTOCOL_VERSION = 1;

    private static final Logger logger = LoggerFactory.getLogger(NetworkMessage.class);
    private static Gson gson = null;

    private transient short protocol = SOCKET_PROTOCOL_VERSION;
    private String id;
    @SerializedName("c")
    private GameCommands cmd;
    @SerializedName("d")
    private String data;
    @SerializedName("s")
    private boolean isSuccess = true;
    @SerializedName("t")
    private long timestamp;

    private transient boolean isIncoming;

    public NetworkMessage(GameCommands cmd, boolean success, String data, String id, short protocol) {
        this.cmd = cmd;
        this.isSuccess = success;
        this.data = data;
        this.id = id;
        this.timestamp = System.nanoTime();
        this.protocol = protocol;
    }

    public NetworkMessage(String id, GameCommands cmd, String data) {
        this(cmd, true, data, id, SOCKET_PROTOCOL_VERSION);
    }

    public NetworkMessage(GameCommands cmd) {
        this(cmd, true, null, null, SOCKET_PROTOCOL_VERSION);
    }

    public NetworkMessage() {

    }


    public static <T> T CreateMessage(String json, Class<T> type) throws InvalidServerMessage {
        try {
            return getGson().fromJson(json, type);
        } catch (Exception e) {
            throw new InvalidServerMessage(json);
        }

    }

    public void setProtocol(short protocol) {
        this.protocol = protocol;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCmd(GameCommands cmd) {
        this.cmd = cmd;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public short getProtocol() {
        return protocol;
    }

    public String getId() {
        return id;
    }

    public GameCommands getCmd() {
        return cmd;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getData() {
        return data;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public void setDataAsJSON(Object o) {
        setData(getGson().toJson(o));
    }

    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(ServerType.class, new ServerTypeSerializer());
            gsonBuilder.registerTypeAdapter(ClaimAwardType.class, new ClaimAwardTypeSerializer());
            gsonBuilder.registerTypeAdapter(GameEndStatus.class, new GameEndStatusSerializer());
            gsonBuilder.registerTypeAdapter(UserTournamentState.class, new UserTournamentStateSerializer());
            gsonBuilder.registerTypeAdapter(UpdateTicketResponse.UpdateTicketReason.class, new UpdateTicketReasonSerializer());
            gsonBuilder.registerTypeAdapter(GameCommands.class, new GameCommandsSerializer());
            gsonBuilder.registerTypeAdapter(PlayerSide.class, new PlayerSideSerializer());
            gsonBuilder.registerTypeAdapter(ErrorCode.class, new ErrorCodeSerializer());
            gsonBuilder.registerTypeAdapter(RoomStatus.class, new RoomStatusSerializer());
            gsonBuilder.registerTypeAdapter(GameStatus.class, new GameStatusSerializer());
            gsonBuilder.registerTypeAdapter(KickType.class, new KickTypeSerializer());
            gsonBuilder.registerTypeAdapter(RemoveUserMessages.class, new RemoveUserMessageSerializer());
            gsonBuilder.registerTypeAdapter(FriendStatus.class, new FriendStatusSerializer());
            gsonBuilder.registerTypeAdapter(TablePairedFilter.class, new TablePairedSerializer());
            gsonBuilder.registerTypeAdapter(TableRobotFilter.class, new TableRobotSerializer());
            gsonBuilder.registerTypeAdapter(TableFullnessFilter.class, new TableFullnessSerializer());
            gsonBuilder.registerTypeAdapter(SettingsTypes.class, new SettingsTypeSerializer());
            gsonBuilder.registerTypeAdapter(InfoCode.class, new InfoCodeSerializer());
            gsonBuilder.registerTypeAdapter(ComparisonTypes.class, new ComparisonTypesSerializer());
            gson = gsonBuilder.create();
        }
        return gson;
    }

    public static NetworkMessage fromString(String s) {
        NetworkMessage response = null;
        try {
            response = getGson().fromJson(s, NetworkMessage.class);
        } catch (JsonSyntaxException ex) {
            logger.error(NetworkMessage.class.getName(), ex);
        } catch (Exception e) {
            logger.error(NetworkMessage.class.getName(), e);
        }
        return response;
    }

    public String toJSON() {
        return getGson().toJson(this);
    }

    public static class ServerTypeSerializer implements JsonSerializer<ServerType>, JsonDeserializer<ServerType> {
        public ServerType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return ServerType.values()[jsonElement.getAsInt()];
        }

        public JsonElement serialize(ServerType serverType, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(serverType.getValue());
        }
    }

    public static class ClaimAwardTypeSerializer implements JsonSerializer<ClaimAwardType>, JsonDeserializer<ClaimAwardType> {
        public ClaimAwardType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return ClaimAwardType.values()[jsonElement.getAsInt()];
        }

        public JsonElement serialize(ClaimAwardType claimAwardType, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(claimAwardType.getValue());
        }
    }

    public static class GameEndStatusSerializer implements JsonSerializer<GameEndStatus>, JsonDeserializer<GameEndStatus> {
        public GameEndStatus deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return GameEndStatus.values()[jsonElement.getAsInt()];
        }

        public JsonElement serialize(GameEndStatus gameEndStatus, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(gameEndStatus.getValue());
        }
    }
    public static class UserTournamentStateSerializer implements JsonSerializer<UserTournamentState>, JsonDeserializer<UserTournamentState> {
        public UserTournamentState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return UserTournamentState.values()[jsonElement.getAsInt()];
        }

        public JsonElement serialize(UserTournamentState userTournamentState, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(userTournamentState.getValue());
        }
    }
    public static class UpdateTicketReasonSerializer implements JsonSerializer<UpdateTicketResponse.UpdateTicketReason>, JsonDeserializer<UpdateTicketResponse.UpdateTicketReason> {
        public UpdateTicketResponse.UpdateTicketReason deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return UpdateTicketResponse.UpdateTicketReason.values()[jsonElement.getAsInt()];
        }

        public JsonElement serialize(UpdateTicketResponse.UpdateTicketReason updateTicketReason, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(updateTicketReason.getValue());
        }
    }

    public static class GameCommandsSerializer implements JsonSerializer<GameCommands>, JsonDeserializer<GameCommands> {
        @Override
        public JsonElement serialize(GameCommands src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }

        @Override
        public GameCommands deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return GameCommands.forCode(typeInt);
        }

    }

    public static class PlayerSideSerializer implements JsonSerializer<PlayerSide>, JsonDeserializer<PlayerSide> {
        @Override
        public JsonElement serialize(PlayerSide src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }

        @Override
        public PlayerSide deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return PlayerSide.forCode(typeInt);
        }
    }

    public static class ErrorCodeSerializer implements JsonSerializer<ErrorCode>, JsonDeserializer<ErrorCode> {
        @Override
        public JsonElement serialize(ErrorCode src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }

        @Override
        public ErrorCode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return ErrorCode.forCode(typeInt);
        }
    }

    public static class InfoCodeSerializer implements JsonSerializer<InfoCode>, JsonDeserializer<InfoCode> {
        @Override
        public JsonElement serialize(InfoCode src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }

        @Override
        public InfoCode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return InfoCode.forCode(typeInt);
        }
    }

    public static class RoomStatusSerializer implements JsonSerializer<RoomStatus>, JsonDeserializer<RoomStatus> {
        @Override
        public JsonElement serialize(RoomStatus src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }

        @Override
        public RoomStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return RoomStatus.forCode(typeInt);
        }
    }

    public static class GameStatusSerializer implements JsonSerializer<GameStatus>, JsonDeserializer<GameStatus> {
        @Override
        public GameStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return GameStatus.forCode(typeInt);
        }

        @Override
        public JsonElement serialize(GameStatus src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }

    public static class KickTypeSerializer implements JsonSerializer<KickType>, JsonDeserializer<KickType> {
        @Override
        public KickType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return KickType.forCode(typeInt);
        }

        @Override
        public JsonElement serialize(KickType src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }

    public static class FriendStatusSerializer implements JsonSerializer<FriendStatus>, JsonDeserializer<FriendStatus> {
        @Override
        public FriendStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return FriendStatus.forCode(typeInt);
        }

        @Override
        public JsonElement serialize(FriendStatus src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }

    public static class TablePairedSerializer implements JsonSerializer<TablePairedFilter>, JsonDeserializer<TablePairedFilter> {
        @Override
        public TablePairedFilter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return TablePairedFilter.forCode(typeInt);
        }

        @Override
        public JsonElement serialize(TablePairedFilter src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }

    public static class TableRobotSerializer implements JsonSerializer<TableRobotFilter>, JsonDeserializer<TableRobotFilter> {
        @Override
        public TableRobotFilter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return TableRobotFilter.forCode(typeInt);
        }

        @Override
        public JsonElement serialize(TableRobotFilter src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }

    public static class TableFullnessSerializer implements JsonSerializer<TableFullnessFilter>, JsonDeserializer<TableFullnessFilter> {
        @Override
        public TableFullnessFilter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return TableFullnessFilter.forCode(typeInt);
        }

        @Override
        public JsonElement serialize(TableFullnessFilter src, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(src.getValue());
        }
    }

    public static class RemoveUserMessageSerializer implements JsonSerializer<RemoveUserMessages>, JsonDeserializer<RemoveUserMessages> {
        @Override
        public RemoveUserMessages deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return RemoveUserMessages.forCode(typeInt);
        }

        @Override
        public JsonElement serialize(RemoveUserMessages src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }

    public static class SettingsTypeSerializer implements JsonSerializer<SettingsTypes>, JsonDeserializer<SettingsTypes> {
        @Override
        public SettingsTypes deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return SettingsTypes.forCode(typeInt);
        }

        @Override
        public JsonElement serialize(SettingsTypes src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }

    public static class ComparisonTypesSerializer implements JsonSerializer<ComparisonTypes>, JsonDeserializer<ComparisonTypes> {
        @Override
        public ComparisonTypes deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int typeInt = json.getAsInt();
            return ComparisonTypes.forCode(typeInt);
        }

        @Override
        public JsonElement serialize(ComparisonTypes src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }


    public NetworkMessage copy() {
        return new NetworkMessage(cmd, isSuccess, data, id, protocol);
    }

    @Override
    public String toString() {
        return "NetworkMessage{" +
                "id='" + id + '\'' +
                ", cmd=" + cmd +
                ", data='" + data + '\'' +
                '}';
    }
}
