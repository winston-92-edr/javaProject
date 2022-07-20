package com.mynet.gameserver;

import com.google.gson.Gson;
import com.mynet.chatserver.models.ChatUser;
import com.mynet.chatserver.models.SetChatUsersModel;
import com.mynet.chatserver.request.ChatEnterTableRequest;
import com.mynet.gameserver.actions.Quick_Start_Game;
import com.mynet.gameserver.actions.SitTableAction;
import com.mynet.gameserver.actions.SitTableActionV2;
import com.mynet.gameserver.builders.InviteResponseBuilder;
import com.mynet.gameserver.enums.*;
import com.mynet.gameserver.model.GiftModel;
import com.mynet.gameserver.model.RemoveGameUserModel;
import com.mynet.gameserver.model.TableUserShortModel;
import com.mynet.gameserver.model.HalfGameModel;
import com.mynet.gameserver.okey.*;
import com.mynet.shared.GetRoomTablesRequest;
import com.mynet.gameserver.response.*;
import com.mynet.gameserver.room.Room;
import com.mynet.gameserver.room.RoomType;
import com.mynet.gameserver.table.TableController;
import com.mynet.gameserver.table.TableWatcherController;
import com.mynet.matchserver.GameUser;
import com.mynet.observer.ObserverCenter;
import com.mynet.observer.ObserverEvents;
import com.mynet.observer.ObserverRunnable;
import com.mynet.proxyserver.network.StringUtil;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.questservice.quests.category.QuestCategory;
import com.mynet.questservice.quests.category.QuestCategoryInfo;
import com.mynet.questservice.quests.category.SocialCategoryInfo;
import com.mynet.questservice.quests.types.SocialActionType;
import com.mynet.shared.analytics.model.SitTableSpecialEvent;
import com.mynet.shared.builders.ErrorResponseBuilder;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.enums.PlayerSide;
import com.mynet.shared.logs.ChatThreadLog;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.logs.TournamentTableLog;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.node.NodeController;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.response.NotifyForAudienceResponse;
import com.mynet.shared.utils.Utils;
import com.mynet.tableservice.service.ServiceProxyDataWrapper;
import com.mynet.tableservice.service.ServiceTableModel;
import com.mynet.tableservice.service.TableUpdateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.launchers.GameServerLauncher;
import com.mynet.shared.connection.NodeToProxy;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.types.GamePlayStatusType;
import com.mynet.shared.types.ServerType;

import javax.naming.CannotProceedException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameController {
    private static Logger logger = LoggerFactory.getLogger(GameController.class);
    private static GameController INSTANCE;
    private NodeToProxy nodeToProxy;
    private NodeController nodeController;
    private TableController tableController;
    private BotController botController;
    private ServerType serverType;
    private Hashtable<Integer, Room> rooms;
    private ServerGlobalVariables globalVariables;
    private ArrayList<RoomType> roomTypeList;
    private Hashtable<String, GiftModel> gifts;
    private int nodeId;

    private ConcurrentHashMap<String, GameUser> users;
    private Gson gson;

    private String groupId;


    public static final int TOURNAMENT_ROOM_ID = 1;

    public static void init(ServerType serverType, NodeController nodeController, int nodeId, String groupId){
        if(INSTANCE == null){
            INSTANCE = new GameController(serverType, nodeController, nodeId, groupId);
        }
    }

    public static GameController getInstance(){
        return INSTANCE;
    }

    public GameController(ServerType serverType, NodeController nodeController, int nodeId, String groupId) {
        this.serverType = serverType;
        this.users = new ConcurrentHashMap<>();
        this.nodeController = nodeController;
        this.nodeToProxy = new NodeToProxy();
        this.tableController = new TableController();
        this.botController = new BotController();
        this.rooms = new Hashtable<>();
        this.globalVariables = ServerGlobalVariables.getInstance();
        this.gson = new Gson();
        this.gifts = DBController.getInstance().getGifts();
        this.nodeId = nodeId;
        this.groupId = groupId;

        TableWatcherController.getInstance().init();

        ObserverCenter.getInstance().addObserver(ObserverEvents.CHANNEL_ACTIVE, new ChannelActive());

        addRedisEventListeners();
    }


    public void initRooms(){
        if(isGeneric()){
            constructTablesAndRooms();
        }else{
            createTournamentRoom();
        }

    }

    public ArrayList<RoomType> getRoomTypeList(){
        return roomTypeList;
    }

    private void constructTablesAndRooms() {
        CacheController cacheController = CacheController.getInstance();
        roomTypeList = DBController.getInstance().getRoomTypes();

        for (RoomType roomType:roomTypeList) {
            roomType.setServerType(serverType);


            Room room = new Room(roomType, roomType.getDefaultTableCount(), this);
            rooms.put(roomType.getId(), room);

            for (int i = 0; i < roomType.getDefaultTableCount(); i++) {
                int withPartner = i % 4 == 0 ? 1 : 0;

                int tableId = cacheController.incAndGetTableCounter();
                createTable("-1", tableId, roomType.getId(), withPartner, 4, TableType.PUBLIC, false);
            }
        }
    }

    public Map<Integer, Integer> getRoomCounts(){

        Map<Integer, Integer> counts = new HashMap<>();
        for (Integer roomId: rooms.keySet()){
            Room room = rooms.get(roomId);
            counts.put(roomId, room.getUsersCount());
        }

        return counts;
    }


    public boolean isGeneric() {
        return serverType == ServerType.GENERIC;
    }

    public void createTournamentRoom() {
        RoomType roomType = new RoomType(1, false, 0, 0, "Tournament", ServerType.TOURNAMENT);
        Room room = new Room(roomType, 0, this);
        rooms.put(TOURNAMENT_ROOM_ID, room);

        roomTypeList = new ArrayList<>();
        roomTypeList.add(roomType);
    }

    public Room getRoom(int roomId) {
        return rooms.get(roomId);
    }

    public ErrorCode addUserToRoomSecure(GameUser user, int roomId) {
        ErrorCode errorCode;
        if (rooms.containsKey(roomId)) {
            if (user.hasRoom()) {
                getRoom(user.getRoomId()).RemoveUser(user);
                user.resetRoom();
            }
            Room room = getRoom(roomId);
            errorCode = room.addUserSecure(user);
        } else {
            errorCode = ErrorCode.WRONG_ROOM_ID;
        }
        return errorCode;
    }

    public int getPlayingTablesCount() {
        return tableController.getPlayingTablesCount().getPlaying();
    }

    public Iterator<GameUser> getUsers(){
        return users.values().iterator();
    }

    public GameUser getUser(String id) {
        return users.get(id);
    }

    private void addRedisEventListeners() {
        logger.info("Game started to listen Redis Events");

        CacheController cacheController = CacheController.getInstance();

        cacheController.listenMuteUpdateEvents((charSequence, userId) -> {
            try {
                GameUser user = getUser((String) userId);
                if(user == null) return;

                Long muted = DBController.getInstance().getUserMuted(user.getId());

                if(muted != null){
                    user.getUserModel().muted = System.currentTimeMillis() < muted;
                    user.getUserModel().muteDate = Utils.dateConversion(muted);
                }
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        });

        cacheController.listenVipUpdateEvents((charSequence, userId) -> {
            try {
                GameUser user = getUser((String) userId);
                if(user == null) return;

                user.updateVip(true);
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        });
        cacheController.listenTicketUpdateEvents((charSequence, userId) -> {
            try {
                GameUser user = getUser((String) userId);
                if(user == null) return;

                int tickets = DBController.getInstance().getUserTickets((String) userId);
                user.updateTicket(tickets);
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        });
        cacheController.listenMoneyUpdateEvents((charSequence, userId) -> {
            try {
                GameUser user = getUser((String) userId);
                if(user == null) return;

                long money = DBController.getInstance().getUserMoney((String) userId);
                user.updateMoney(money);

                addUserToTableService(user.getBasicUser());
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        });

        cacheController.listenTableNodeDeleteEvent((charSequence, nodeId) -> {
            Node tableNode = nodeController.getTableNode();
            if(tableNode != null && tableNode.getId() == nodeId) nodeController.removeNode(nodeId);
        });

        cacheController.listenTableNodeAddEvent((charSequence, nodeData) -> {
            if(serverType.equals(ServerType.GENERIC) && nodeData.getGroupId().equals(groupId)) {
                nodeController.addNode(nodeData);
            }
        });

        cacheController.listenTablesReset((charSequence, event)->{
            List<Table> tables = getTables();

            for (Table table : tables){
                addToTableService(table, true);
            }

            logger.info("[TABLE RESET LISTEN]");
        });

        cacheController.listenFixTableEvent((charSequence, tableId)->{
            Table table = getTable(tableId);
            if(table == null) return;

            addToTableService(table, true);
        });

        cacheController.listenRemoveGameUser((charSequence, removeGameUserModel) -> {
            if(removeGameUserModel.getGameNodeId() == getNodeId()) return;

            removeUser(removeGameUserModel.getUserId(), false);
        });

        cacheController.listenChatNodeAddEvent((charSequence, nodeData)->{

            if(serverType.equals(ServerType.GENERIC) && nodeData.getGroupId().equals(groupId)){

                ArrayList<ChatUser> chatUsers = new ArrayList<>();

                List<Table> tables = getTables();

                for (Table table : tables){
                    Enumeration<GameUser> gameUsers = table.getAllUsers();
                    while(gameUsers.hasMoreElements()){
                        GameUser gameUser = gameUsers.nextElement();
                        boolean audience = table.isAudienceOrGamer(gameUser.getId()) == 2;
                        ChatUser chatUser = new ChatUser(gameUser.getId(), gameUser.getTableId(), gameUser.getProxyId(), gameUser.getUserModel().muteDate, audience, gameUser.getName(), gameUser.getIp(), gameUser.getRoomId());
                        chatUsers.add(chatUser);
                    }
                }

                SetChatUsersModel setChatUsersModel = new SetChatUsersModel(groupId, chatUsers.toArray());
                cacheController.publishSetChatUsers(setChatUsersModel);
            }
        });

        cacheController.listenUpdateSessionId((charSequence, info) -> {
            String[] data = info.split(":");

            if (data.length < 2) return;

            String id = data[0];
            String sessionId = data[1];

            GameUser user = getUser(id);
            if (user == null) return;

            user.setAnalyticsSessionId(sessionId);
        });
    }

    public GameUser getUser(long id) {
        return users.get(id + "");
    }
    public int getUsersCount() {
        return users.size();
    }

    public boolean addUser(GameUser user) {
        String userID = user.getId();

        if (users.containsKey(userID)) {
            return false;
        }

        users.put(userID, user);
        CacheController.getInstance().setGameNode(user.getId(), nodeId);
        user.setGameId(nodeId);

        addUserToTableService(user.getBasicUser());

        return true;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public GameUser createUser(String userId) throws CannotProceedException {
        removeUser(userId, false);

        //TODO: check redis first
        UserModel model = DBController.getInstance().getUser(userId);
        GameUser user = new GameUser(model);

        HashSet<String> gifts = DBController.getInstance().getUserGifts(userId);
        user.setGifts(gifts);

        UserModel userModel = CacheController.getInstance().getUserGameModel(userId);
        user.setAnalyticsSessionId(userModel.analyticsSessionId);
        user.setAnalyticsDeviceId(userModel.analyticsDeviceId);
        user.setApplicationVersion(userModel.applicationVersion);

        String platform = CacheController.getInstance().getUserLastPlatform(userId);
        user.setPlatform(platform);

        addUser(user);

        CacheController.getInstance().incNodeUserCounter(nodeId, userId);
        CacheController.getInstance().publishRemoveGameUser(new RemoveGameUserModel(nodeId, user.getId()));

        return user;
    }

    public void removeUser(String userId, boolean clearCache) {
        GameUser user = getUser(userId);
        CacheController cacheController = CacheController.getInstance();
        if(user != null){
            removeFromTable(user);
            int roomId = user.getRoomId();
            Room room = getRoom(roomId);
            if(room != null){
                room.RemoveUser(user);
            }

            users.remove(userId);
        }

        cacheController.decNodeUserCounter(nodeId, userId);

        if(clearCache){
            cacheController.resetUserGameAndTable(userId);
        }
    }

    private void removeFromTable(GameUser user) {
        try {
            int tableId = user.getTableId();
            if (tableId == -1) {
                return;
            }
            Table table = GameController.getInstance().getTable(tableId);
            if (table != null) {
                if (table.containsUser(user)) {
                    table.removeUser(user, true);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public boolean isUserExist(String userId){
        return users.containsKey(userId);
    }

    public void fixUser(String userId) {
        if(!users.containsKey(userId)){
            try {
                createUser(userId);
            }catch (CannotProceedException e){
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void addTable(Table table){
        tableController.addTable(table);
    }

    public GamePlayStatusType createTable(String owner, int tableId, int roomId, int withPartner, int sideCount, TableType tableType, boolean dynamical) {
        try {
            GamePlayStatusType gamePlayStatus = GamePlayStatusType.VALID;
            Room room = getRoom(roomId);
            if (room != null) {

                OkeyTable okeyTable = new OkeyTable(tableId, room.getRealRoomType(), room.getBet(), dynamical, withPartner, room.getRealRoomType().getVipMode(), owner, sideCount, tableType);
                addTable(okeyTable);
                room.AddTable(okeyTable.getTableId(), okeyTable);
                okeyTable.setRoomId(roomId);
                TableWatcherController.getInstance().addTable(okeyTable);

                //for manage all tables from Table Service
                addToTableService(okeyTable, false);

                if(serverType.equals(ServerType.TOURNAMENT)){
                    TournamentTableLog tableLog = new TournamentTableLog.Builder()
                            .activeTables(tableController.getPlayingTablesCount().getPlaying())
                            .inactiveTables(tableController.getPlayingTablesCount().getWaiting())
                            .tableCount(TableWatcherController.getInstance().getTableArrayCount())
                            .removingTableCount(TableWatcherController.getInstance().getRemoveTableArrayCount())
                            .tableSize(tableController.getSize())
                            .build();

                    RabbitMQLogController.getInstance().addTournamentTableLog(tableLog);
                }

            } else {
                gamePlayStatus = GamePlayStatusType.WRONG_ROOM_ID;
                logger.error("ERROR at createTable | room is NULL | table: " + tableId);
            }

            return gamePlayStatus;
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

        return GamePlayStatusType.GENERAL_ERROR;
    }

    public void sitTableWithAction(GameUser user, NetworkMessage response, int tableId, int roomId, PlayerSide side, boolean isFromQuickPlay, String from) {
        try {
            if (isInMaintenance()) {
                nodeToProxy.sendError(user, ErrorCode.MAINTENANCE_MODE);
                return;
            }

            if (roomId != -1 && roomId != user.getRoomId()) {
                ErrorCode errorCode = addUserToRoomSecure(user, roomId);
                if (errorCode == null) {
                    user.setRoomId(roomId);
                } else {
                    nodeToProxy.sendError(user, errorCode);
                    return;
                }
            }

            Table table = getTable(tableId);

            if (table == null) {
                sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.TABLE_NULL)));
                return;
            }

            boolean isLowBet = getRoom(user.getRoomId()).isLowBet(user.getMoney());

            if ((user.getMoney() >= table.getMinBet() && !isLowBet) || (table.availableToBotSit(side.getValue(), user))) {
                table.addTableAction(new SitTableAction(table, user, tableId, side, isFromQuickPlay, from));
            } else if (!table.canEnterToVipTable(user) && user.getMoney() >= table.getMinBet() && !isLowBet) {
                ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.NOT_VIP_TABLE).createErrorResponse();
                sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(error));
            } else if(isLowBet) {
                ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.LOW_BET_TABLE).createErrorResponse();
                sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(error));
           } else {
                ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.NOT_ENOUGH_MONEY_TABLE).createErrorResponse();
                sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(error));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

    }

    public void sitTableV2WithAction(GameUser user, NetworkMessage response, int tableId, int roomId, int side, boolean isFromQuickPlay) {
        try {
            if (isInMaintenance()) {
                getNodeToProxy().sendError(user, ErrorCode.MAINTENANCE_MODE);
                return;
            }
            Table table = getTable(tableId);
            if (table != null) {
                table.addTableAction(new SitTableActionV2(table, user, tableId, side, false));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void sendTablesInfoRequest(String roomId, String userId){
        Node tableNode = nodeController.getTableNode();
        if(tableNode == null)return;

        NetworkMessage networkMessage = new NetworkMessage(GameCommands.TABLES_INFO);

        ServiceProxyDataWrapper wrapper = new ServiceProxyDataWrapper(roomId, 8);
        networkMessage.setData(gson.toJson(wrapper));
        networkMessage.setId(userId);
        tableNode.sendRequest(networkMessage, null);
    }

    public void updateTableService(int tableId, TableUpdateType type, GameUser user, int side, long pot, int roomId){
        Node tableNode = nodeController.getTableNode();
        if(tableNode == null)return;

        NetworkMessage networkMessage = new NetworkMessage(GameCommands.UPDATE_TABLE);
        BasicUserModel userModel = null;
        if(user != null){
            userModel = new BasicUserModel(user.getfuid(), user.getName(), user.getPlatform(), user.getMoney(), user.getIsVip());
            userModel.setRoomId(roomId);
            userModel.setProxyId(user.getProxyId());
        }


        TableUpdateWrapper wrapper = new TableUpdateWrapper(tableId, type, side, userModel);
        wrapper.setPot(pot);

        String json = gson.toJson(wrapper);
        networkMessage.setData(json);
        tableNode.sendRequest(networkMessage, null);
    }



    public void addToTableService(Table table, boolean withUsers){
        Node tableNode = nodeController.getTableNode();
        if(tableNode == null)return;

        NetworkMessage networkMessage = new NetworkMessage(GameCommands.ADD_TABLE);

        ServiceTableModel model = new ServiceTableModel();
        model.setTableId(table.getTableId());
        model.setBet(table.getBet());
        model.setMinBet(table.getMinBet());
        model.setPaired(table.getIsPartner());
        model.setTableType(table.getTableType());
        model.setGameServerId(GameServerLauncher.currentNode.getId());
        model.setRoomId(table.getRoomId());
        model.setPotValue(table.getPotValue());


        if(withUsers){
            for (int side = 0; side < table.getSideCount(); side++) {
                GameSide gameSide = table.getGameSide(side);
                if(gameSide == null){
                    model.setSide(side, null);
                    continue;
                }

                GameUser user = gameSide.getUser();
                if(user == null){
                    model.setSide(side, null);
                }else{
                    long userId = Long.parseLong(user.getfuid());
                    if(userId < 1000){
                        model.setSide(side, true);
                    }else{
                        model.setSide(side, user.getBasicUser());
                    }
                }
            }
        }

        String json = gson.toJson(model);
        networkMessage.setData(json);

//        if(quick){
//            tableNode.sendQuickRequest(networkMessage, null);
//        }else{
//        }
        logger.info(String.format("Adding table from node: %d => %s", nodeId, model.toString()));
        tableNode.sendRequest(networkMessage, null);
    }

    public void addUserToTableService(BasicUserModel user){
        Node tableNode = nodeController.getTableNode();
        if(tableNode == null)return;

        NetworkMessage networkMessage = new NetworkMessage(GameCommands.ADD_USER);

        String json = gson.toJson(user);
        networkMessage.setData(json);
        tableNode.sendRequest(networkMessage, null);
    }

    public void addUserTableServiceRooms(BasicUserModel user){
        Node tableNode = nodeController.getTableNode();
        if(tableNode == null)return;

        NetworkMessage networkMessage = new NetworkMessage(GameCommands.ADD_USER_TO_ROOM);

        String json = gson.toJson(user);
        networkMessage.setData(json);
        tableNode.sendRequest(networkMessage, null);
    }

    public void removeUserTableServiceRooms(BasicUserModel user){
        Node tableNode = nodeController.getTableNode();
        if(tableNode == null)return;

        NetworkMessage networkMessage = new NetworkMessage(GameCommands.REMOVE_USER_FROM_ROOM);

        String json = gson.toJson(user);
        networkMessage.setData(json);
        tableNode.sendRequest(networkMessage, null);
    }

    public void removeFromTableService(String tableId, int roomId){
        Node tableNode = nodeController.getTableNode();
        if(tableNode == null)return;

        NetworkMessage networkMessage = new NetworkMessage(GameCommands.REMOVE_TABLE);

        networkMessage.setData(tableId+ ";" + roomId);
        tableNode.sendRequest(networkMessage, null);
    }

    public Table getTable(String tableId){
        return getTable(Integer.parseInt(tableId));
    }

    public List<Table> getTables(){
        return tableController.getTables();
    }

    public Table getTable(int tableId){
        return tableController.getTable(tableId);
    }

    public void removeTable(Table table) {
        table.killTread();
        tableController.removeTable(table);
        TableWatcherController.getInstance().deleteTable(table);
        removeFromTableService(table.getTableId() + "", table.getRoomId());
    }

    public NodeToProxy getNodeToProxy() {
        return nodeToProxy;
    }

    public BotController getBotController(){
        return botController;
    }

    public boolean isInMaintenance() {

        String key = isGeneric() ? "GENERIC_MAINTENANCE_MODE" : "TOURNAMENT_MAINTENANCE_MODE";

        String mode = globalVariables.getString(key, "false");

        return mode.equals("true");
    }

    public String getMaintenanceMessage() {
        return "Sunucu şu an bakımda. Lütfen daha sonra tekrar dene.";
    }


    public String getVersion() {
        //TODO: add version
        return "Game1";
    }

    public void joinAnAudience(GameUser user, String tableId, String from) {
        try {
            if (user == null) {
                return;
            }
            if (user.getTableId() != -1) {
                sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.CANNOT_AUDIENCE)));
                return;
            }

            Table table = getTable(tableId);

            if (table == null) {
                sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.TABLE_NULL)));
                return;
            }

            boolean isLowBet = getRoom(table.getRoomId()).isLowBet(user.getMoney());

            if(isLowBet) {
                ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.LOW_BET_TABLE).createErrorResponse();
                sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(error));
                return;
            }

            if(!user.getIsVip() && user.getMoney() < table.getMinBet()){
                ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.NOT_ENOUGH_MONEY_TABLE).createErrorResponse();
                sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(error));
                return;
            }


            if(!table.canAcceptAudience()){
                sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.AUDIENCE_LIMIT)));
                return;
            }

            if (table.addAudience(user)) {
//                user.resetGift();
                NotifyForAudienceResponse userdata = new NotifyForAudienceResponse(user);

                table.notifyForAudience(NetworkMessage.getGson().toJson(userdata), user.getfuid());

                String roomName = rooms.get(table.getRoomId()).getRoomType().getName();
                EnterTableResponse message = new EnterTableResponse(table,true, -1,roomName, true);
                sendNetworkMessage(user, GameCommands.ENTER_TABLE, NetworkMessage.getGson().toJson(message));

                ChatEnterTableRequest chatRequest = new ChatEnterTableRequest(table.getTableId(), table.getRoomId(), true);
                sendNetworkMessage(user, GameCommands.CHAT_ENTER_TABLE, NetworkMessage.getGson().toJson(chatRequest));

                if(table.getGameStatus().equals(GameStatus.PLAYING)) {
                    table.sendUserGameLog(3, true, user, table.getPotValue(), false);
                }else{
                    user.setTableEvent(new SitTableSpecialEvent(table.getGameId(), from));
                }

                int roomID = table.getRoomId();
                if (user.getRoomId() == -1) {
                    Room room = getRoom(roomID);
                    room.AddUser(user);
                }

                user.setTableId(table.getTableId());

                int cntBU = table.getUserCount() + table.getBotCount();
                if (table.getGameStatus() != GameStatus.NOTSTARTED && table.getGameStatus() != GameStatus.GET_READY && cntBU == table.getSideCount()) {
                    TableInfoResponse tableData = table.getTableData(-1);

                    sendNetworkMessage(user, GameCommands.TABLE_INFO, NetworkMessage.getGson().toJson(tableData));
                    sendNetworkMessage(user, GameCommands.GAME_ID, NetworkMessage.getGson().toJson(new GameIdResponse(table.getGameId())));
                }

                table.sendUserSitTableSpecialEvent(user, from, "Sit Table", true);
            } else {
                sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.CANNOT_AUDIENCE)));
            }
        } catch (Exception e) {
            sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.CANNOT_AUDIENCE)));
            logger.error(e.getMessage(), e);
        }
    }

    public void sendNetworkMessage(GameUser user, GameCommands command, String data){
        NetworkMessage networkMessage = new NetworkMessage(command);
        networkMessage.setData(data);
        nodeToProxy.addServerMessage(networkMessage, user);
    }

    public void startHalfGames() {
        List<HalfGameModel> halfGames = CacheController.getInstance().getAndClearHalfGames(nodeId);

        Thread halfGameThread = new Thread(() -> {
            try {
                Thread.sleep(500);

                for (HalfGameModel game: halfGames){

                    //first: create users no exist
                    boolean success = true;
                    try {
                        for (long id: game.getUsers()){
                            if(id == -1 || id == -2) continue;

                            String userId = id + "";
                            if(getUser(userId) == null){
                                createUser(userId);
                            }
                            GameUser user = getUser(userId);

                            sendNetworkMessage(user , GameCommands.CHANGE_GAME_NODE, nodeId+"");
                        }
                    }catch (Exception e){
                        success = false;
                    }

                    if(!success) continue;

                    logger.info("new TABLE created FOR: " + game.getTableId());

                    GamePlayStatusType createStatus = createTable(game.getUsers()[0]+"", game.getTableId(), game.getRoomId(), game.getPaired(), game.getSideCount(), game.isPrivateTable() ? TableType.PRIVATE : TableType.PUBLIC, true);
                    if(createStatus == GamePlayStatusType.VALID){
                        Table table = getTable(game.getTableId());
                        table.setGameStatus(GameStatus.NOTSTARTED);
                        table.setPotValue(game.getPot());
                        table.setDealerSide(game.getDealerSide());
                        table.setRemainingTime(game.getTableTimerModel());
                        table.setGameStatus(GameStatus.forCode(game.getState()));
                        table.setCardHandler(game.getCardHandler());
                        table.setStartingValues(game.isFirstTurn(), game.getGameId());
                        table.setSideState(game.getSideState());

                        for (int side = 0; side < game.getUsers().length ; side++) {
                            long userId = game.getUsers()[side];
                            if(userId == -1){
                                Bot bot = getBotController().createBot(table, side);
                                table.sitBot(side, bot);
                            }else if(userId != -2){
                                GameUser user = getUser(userId);
                                table.addTableAction(new SitTableAction(table, user, game.getTableId(), PlayerSide.forCode(side), false, "HALF_GAMES"));
                            }
                        }

                        table.setDoubles(game.getDoubles());
                    }
                }

                logger.info("Game SERVER Half Games Set");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        halfGameThread.start();


    }

    public RoomType getSmallestRoomType() {
        return this.roomTypeList.get(0);
    }

    public RoomType getBiggestRoomType() {
        return this.roomTypeList.get(this.roomTypeList.size() - 1);
    }

    public int[] getAvailableRoom(GameUser user) {
        long userMoney = user.getMoney();
        int quickPlayRoomDown = globalVariables.getInt("quickPlayRoomDown", 1);
        int quickPlayRange = globalVariables.getInt("quickPlayRange", 4);
        int bigRoom = getBiggestRoomType().getId();
        int smallRoom = getSmallestRoomType().getId();

        ArrayList<RoomType> checkRange = new ArrayList<>();


        // add to list as quickPlayRange
        int rangeCounter = 0;
        for (int i = bigRoom; i >= smallRoom; i--) {
            Room room = getRoom(i);
            RoomType roomType = room.getRealRoomType();

            if (rangeCounter <= quickPlayRange) {
                if (userMoney >= roomType.getMinBet()) {
                    checkRange.add(roomType);
                    rangeCounter++;
                }
            } else {
                break;
            }
        }

        // remove as quickPlayRoomDown size
        if (checkRange.size() >= quickPlayRoomDown + 1) {
            if (quickPlayRoomDown > 0) {
                checkRange.subList(0, quickPlayRoomDown).clear();
            }
        }


        for (int c = 3; c > 0; c--) {
            for (RoomType roomType : checkRange) {
                Room room = getRoom(roomType.getId());

                int tableId = room.getPlayNowQp(c);

                if (tableId > -1) {
                    return new int[]{tableId, roomType.getId()};
                }
            }
        }

        // if no table found: return first roomId
        if (checkRange.size() > 0) {
            return new int[]{-1, checkRange.get(0).getId()};
        } else {
            return new int[]{-1, -1};
        }
    }

    public void sitPlayNowTable(int tableId, GameUser user) {

        try {
            Table table = getTable(tableId);
            enterRoom(table.getRoomId(), user);
            if (user.getPlatform().equals("web")) {
                sendNetworkMessage(user, GameCommands.QUICK_PLAY, "" + tableId + ";" + user.getRoomId());
            } else {
                sendNetworkMessage(user, GameCommands.QUICK_PLAY, "" + tableId);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            sendNetworkMessage(user, GameCommands.QUICK_PLAY, "-1");
        }
    }

    public int getUserBestRoom(long money) {
        Enumeration<Integer> roomIds = rooms.keys();
        int bestRoom = getSmallestRoomType().getId();

        while (roomIds.hasMoreElements()) {
            Integer roomId = roomIds.nextElement();
            Room r = this.rooms.get(roomId);
            if (r.isVip()) continue;

            if (money > r.getMinBet()) {

                if (roomId > bestRoom) bestRoom = roomId;

            }

        }

        if (bestRoom < 1) bestRoom = getSmallestRoomType().getId();

        return bestRoom;
    }

    public boolean enterRoom(int roomId, GameUser user) {
        try {

            if (roomId == -1) {
                return false;
            }else if(roomId == -1 || roomId == 0){
                roomId = getUserBestRoom(user.getMoney());
            }

            Room room = getRoom(roomId);

            if (room == null) {
                return false;
            }

            if (user.getRoomId() != 0) {
                leaveRoom(user);
            }

            int moneyLimit = room.getMinBet();
            boolean isLowBet = room.isLowBet(user.getMoney());

            EnterRoomResponse.Builder builder = new EnterRoomResponse.Builder();
            if (room.isVip() && !user.getIsVip()) {
                ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.NOT_VIP_ROOM).createErrorResponse();
                sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(error));
            }else if (!user.getIsVip() && user.getMoney() < moneyLimit) {
                sendTableServiceGetRoomTables(user,roomId);
            } else if(isLowBet) {
                ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.LOW_BET_ROOM).createErrorResponse();
                sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(error));
            }else {
                room.AddUser(user);
                EnterRoomResponse response = builder.setStatus(RoomStatus.VALID).setRoomId(user.getRoomId()).build();
                sendNetworkMessage(user, GameCommands.ENTER_ROOM, NetworkMessage.getGson().toJson(response));
                return true;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        EnterRoomResponse.Builder builder = new EnterRoomResponse.Builder();
        EnterRoomResponse response = builder.setStatus(RoomStatus.ERROR).setRoomId(user.getRoomId()).build();
        sendNetworkMessage(user, GameCommands.ENTER_ROOM, NetworkMessage.getGson().toJson(response));
        return false;
    }

    public void leaveRoom(GameUser user) {
        try {
            int roomId = user.getRoomId();
            Room room = getRoom(roomId);
            if (room != null) {
                room.RemoveUser(user);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public int openTableQuickPlay(String data, GameUser user) {

        StringTokenizer str = new StringTokenizer(data, ";");
        Integer roomId = Integer.parseInt(str.nextToken());
        Integer withPartner = Integer.parseInt(str.nextToken());

        Room room = getRoom(roomId);

        if (room == null) {
            return -1;
        }

        if (room.isVip() && !user.getIsVip()) {
            return -1;
        }

        if (roomId < 1) {
            return -1;
        }

        int bet = room.getBet();
        int mimConnectBet = bet * 3;

        if (user.getMoney() < mimConnectBet) {
            return -1;
        }

        try {
            int tableId = CacheController.getInstance().incAndGetTableCounter();
            GamePlayStatusType error = createTable(user.getfuid(), tableId, roomId, withPartner, 4, TableType.PUBLIC, true);
            if (error == GamePlayStatusType.VALID) {
                return tableId;
            }
            return -1;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1;

        }

    }

    public void sendOpenTableWithAction(GameUser user, int roomId, int withPartner) {

        try {
            Room room = getRoom(roomId);
            ErrorCode errorCode = Quick_Start_Game.canSendOpenTable(user, room);
            if (errorCode == null) {

                int tableId = CacheController.getInstance().incAndGetTableCounter();
                GamePlayStatusType error = createTable(user.getfuid(), tableId, roomId, withPartner, 4, TableType.PUBLIC, true);

                if (error == GamePlayStatusType.VALID) {
                    Table table = getTable(tableId);

                    table.addTableAction(new SitTableAction(table, user,table.getTableId(), PlayerSide.forCode(-1), false, "OPEN_TABLE"));
                    return;

                } else {
                    logger.error("Error:" + error.getMsg() + " roomId:" + roomId);

                    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.CANNOT_OPEN_TABLE);
                    sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(errorResponse));
                }
            } else {
                ErrorResponse errorResponse = new ErrorResponse(errorCode);
                sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(errorResponse));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    public List<GameUser> getUsersInLobby() {
        List<GameUser> lobbyUsers = users.values().stream().filter(x -> x.getTableId()<= 0).collect(Collectors.toList());
        return lobbyUsers;
    }

    public List<TableUserShortModel> getUsersInLobby(GameUser user) {
        List<TableUserShortModel> userList = new ArrayList<>();
        Enumeration<GameUser> en = users.elements();
        Table table = tableController.getTable(user.getTableId());
        int minBet = table.getMinBet();

        while (en.hasMoreElements()) {
            GameUser otherUser = en.nextElement();
            if (otherUser.getTableId() <= 0 /*&& otherUser.getUserModel().closedInvite == 0*/ && otherUser.getMoney() >= minBet) {

                TableUserShortModel userInfo = new TableUserShortModel(otherUser.getfuid(), otherUser.getMoney(), otherUser.getExperiencePoints(), otherUser.getName(), otherUser.getIsVip());
                userList.add(userInfo);
            }
        }
        return userList;
    }

    public void inviteUser(String tableId, String invitedFuid, String name, String senderFuid) {
        GameUser sender = users.get(senderFuid);

        if (isGeneric() && sender != null) {
            GameUser invited = users.get(invitedFuid);
            name = StringUtil.correctTurkish(name);
            Table table = getTable(tableId);

            if (table != null) {
                InviteResponseBuilder builder = new InviteResponseBuilder();
                InviteResponse response = builder.setGameNode(nodeController.getCurrentNode().getId())
                        .setName(name)
                        .setRoomId(sender.getRoomId())
                        .setTableId(Integer.parseInt(tableId))
                        .setSenderId(senderFuid)
                        .setBet(table.getBet())
                        .setPartner(table.getIsPartner() == 1)
                        .setPot(table.getPotValue())
                        .setUsers(table.getInviteTableUsers())
                        .createInviteResponse();

                if (invited != null) {
                    //if (invited.getUserModel().closedInvite == 0*/) {

                        if (invited.getTableId() != -1 || (table.getIsVip() && !invited.getIsVip())) {
                            return;
                        }

                        Room room = rooms.get(table.getRoomId());

                        if(room != null && room.isLowBet(invited.getMoney())) return;

                        sendNetworkMessage(invited, GameCommands.SEND_INVITE_REQUEST, NetworkMessage.getGson().toJson(response));
                    //}
                } else {

                    int invitedProxy = CacheController.getInstance().getUserProxyNode(invitedFuid);

                    if (invitedProxy != -1) {
                        NetworkMessage networkMessage = new NetworkMessage(GameCommands.CHECK_AVAILABLE_INVITE);
                        networkMessage.setDataAsJSON(response);
                        networkMessage.setId(invitedFuid);
                        nodeToProxy.addServerMessage(networkMessage, invitedProxy);
                    }
                }
            }
        }
    }

    public GiftModel getGift(String giftId){
        return gifts.get(giftId);
    }

    public int getNodeId() {
        return nodeId;
    }

    public void sendTableChatMessage(String userId, String message) {
        try {

            GameUser user = this.getUser(userId);

            boolean muted= false;

            if(user.getUserModel().muteDate != null) {
                Date muteDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(user.getUserModel().muteDate);
                muted = System.currentTimeMillis() < muteDate.getTime();
            }

            if(!muted) {
                int tableId = user.getTableId();

                if (tableId == -1) {
                    logger.warn("TABLE ID is -1 WHILE TRYING SEND CHAT MESSAGE FOR USER: " + userId);
                    return;
                }

                Table table = getTable(tableId);

                if (table != null) {
                    Enumeration<GameUser> allUsers = table.getAllUsers();
                    while (allUsers.hasMoreElements()) {
                        GameUser gameUser = allUsers.nextElement();

                        if (gameUser != null) {
                            boolean isAudience = table.isAudienceOrGamer(user.getId()) == 2;
                            TableChatResponse response = new TableChatResponse(userId, message, user.getName(), isAudience);
                            sendNetworkMessage(gameUser, GameCommands.TABLE_CHAT, NetworkMessage.getGson().toJson(response));
                        }
                    }

                    String ex = "ODA: " + table.getRoomId();
                    if (user.getBasicUser().getRoomId() == 14) {
                        ex = "MLY: ";
                    }

                    String chatSource = ex + " - MASA: " + table.getTableId() + " ";
                    String chatLine = getChatLine(userId, message, chatSource, user.getIp());

                    RabbitMQLogController.getInstance().addChatLog(new ChatThreadLog(String.valueOf(nodeId), chatLine));

                    QuestCategoryInfo questCategoryInfo = new SocialCategoryInfo(user.getBasicUser().getId(), SocialActionType.SEND_TABLE_MESSAGE.getValue());
                    RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.SOCIAL, questCategoryInfo);

                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private String getChatLine(String fuid, String msg, String source, String ip) {
        Date date = new Date();
        SimpleDateFormat ft2 = new SimpleDateFormat("hh:mm:ss");
        String newDate2 = ft2.format(date);

        msg = StringUtil.normalTurkish(msg);

        return newDate2 + " # " + fuid + ": " + msg + " # " + source + ":" + ip + "\n";
    }

    public String getRoomName(int id){
        return rooms.get(id).getRoomType().getName();
    }

    private class ChannelActive extends ObserverRunnable {

        private final String id;

        public ChannelActive() {
            this.id = "ChannelActive_" + System.currentTimeMillis();
        }

        @Override
        protected void setData(Object data) {
        }

        @Override
        protected String getId() {
            return id;
        }

        @Override
        public void run() {
            logger.info("CHANNEL IS ACTIVE NOW FOR NODE: " + nodeId);

            Iterator<GameUser> users = getUsers();

            while(users.hasNext()){
                GameUser user = users.next();
                addUserToTableService(user.getBasicUser());
            }

            List<Table> tables = getTables();

            for (Table table : tables){
                addToTableService(table, true);
            }

            ObserverCenter.getInstance().emit(ObserverEvents.RELEASE_CHANNEL);
        }
    }

    public void sendTableServiceGetRoomTables(GameUser user, int roomId){
        Node tableNode = nodeController.getTableNode();
        if(tableNode == null)return;

        NetworkMessage networkMessage = new NetworkMessage(GameCommands.GET_ROOM_TABLES);

        String json = gson.toJson(new GetRoomTablesRequest(roomId,user.getId(), user.getProxyId()));
        networkMessage.setData(json);
        tableNode.sendRequest(networkMessage, null);
    }
}
