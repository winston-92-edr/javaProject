package com.mynet.tableservice.service;

import com.google.gson.Gson;
import com.mynet.gameserver.enums.TableType;
import com.mynet.gameserver.model.AvailableTableModel;
import com.mynet.gameserver.model.TableFilterModel;
import com.mynet.gameserver.model.TableInfoModel;
import com.mynet.socialserver.model.RoomUserCountModel;
import com.mynet.tableservice.enums.TableFullnessFilter;
import com.mynet.tableservice.enums.TablePairedFilter;
import com.mynet.tableservice.enums.TableRobotFilter;
import com.mynet.gameserver.room.Room;
import com.mynet.gameserver.room.RoomType;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.connection.NodeToProxy;
import com.mynet.shared.logs.*;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.types.ServerType;
import com.mynet.tableservice.RoomUsersController;
import com.mynet.tableservice.model.QuickPlayRoomModel;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

public class TableService {
    private static Logger logger = LoggerFactory.getLogger(TableService.class);
    private static TableService instance;
    private CacheController cacheController;

    private Gson gson;


    public static void init(String groupId, CacheController cacheController) {
        if (instance == null) {
            instance = new TableService(groupId, cacheController);
        }
    }

    public static TableService getInstance() {
        return instance;
    }

    private ConcurrentHashMap<Integer, ServiceTableModel> tables;
    private HashMap<String, ServiceUser> users;
    private ConcurrentHashMap<Integer, TreeSet<ServiceTableModel>> orderedTables;
    private ArrayList<RoomType> roomTypes;
    private HashMap<Integer, Room> rooms;
    private NodeToProxy nodeToProxy;
    private RoomUsersController roomUsersController;

    public TableService(String groupId, CacheController cacheController) {
        this.tables = new ConcurrentHashMap<>();
        this.users = new HashMap<>();
        this.orderedTables = new ConcurrentHashMap<>();
        this.nodeToProxy = new NodeToProxy();
        this.rooms = new HashMap<>();
        this.gson = new Gson();
        this.cacheController = cacheController;

        RabbitMQLogController.init();
        initRooms();

        this.roomUsersController = new RoomUsersController(groupId);
    }

    public List<NodeData> getGameNodes(){
        return roomUsersController.getGameNodes();
    }

    public ServiceTableModel getTable(int tableId) {
        return tables.get(tableId);
    }

    public void sendNetworkMessage(String userId, int proxyId, NetworkMessage networkMessage) {
        this.nodeToProxy.addServerMessage(networkMessage, userId, proxyId);
    }

    public void addTable(ServiceTableModel model) {
        tables.put(model.getTableId(), model);
        ConcurrentHashSet<Integer> roomTableSet = this.rooms.get(model.getRoomId()).getTableSet();
        roomTableSet.add(model.getTableId());

        logger.info("SERVICE >> NEW TABLE: " + model.getTableId());
    }

    public void removeTable(int id, int roomId) {
        tables.remove(id);

        if(roomId == -1){
            for (RoomType roomType: roomTypes){
                ConcurrentHashSet<Integer> roomTableSet = this.rooms.get(roomType.getId()).getTableSet();
                if(roomTableSet != null && roomTableSet.contains(id)){
                    roomTableSet.remove(id);
                    break;
                }
            }
        }else{
            ConcurrentHashSet<Integer> roomTableSet = this.rooms.get(roomId).getTableSet();
            if (roomTableSet != null) {
                roomTableSet.remove(id);
            }
        }

        if (tables.size() == 0) {
            logger.info("SERVICE >> CURRENT TABLES is empty");
        }
    }

    public List<TableInfoModel> getTablesInfo(String userId, int roomId, TablePairedFilter paired, TableRobotFilter robot, TableFullnessFilter fullness) {
        List<TableInfoModel> tables = new ArrayList<>();
        ServiceUser user = getUser(userId);
        if (user == null) {
            //TODO: create user
            logger.warn("NO USER WHILE GETTING TABLES INFO FOR USER: " + userId);
            return null;
        }

        TableFilterModel filter = new TableFilterModel(paired, robot, fullness);

        if (roomId == -1){
            tables = getAvailableTables(user);

            tables = tables.stream().filter(x -> x.filterTable(filter)).collect(Collectors.toList());

            tables.sort((o1, o2) -> o2.getGamers().size() - o1.getGamers().size());

            System.out.println("Sorted (-1) tables in getTablesInfo: " + NetworkMessage.getGson().toJson(tables));

            return tables;
        }

        TreeSet<ServiceTableModel> treeSet = orderedTables.get(roomId);
        for (ServiceTableModel model : treeSet) {

            System.out.println("Table ID: " + model.getTableId() + " -> isFullnessAvailable: " + model.isFullnessAllAvailable());

            if(!fullness.equals(TableFullnessFilter.ALL) || (fullness.equals(TableFullnessFilter.ALL) && model.isFullnessAllAvailable())) {
                int gameNode = getTableGameNode(model.getTableId());
                if (gameNode == -1) continue;

                if (model.getTableType().equals(TableType.PUBLIC)) {
                    TableInfoModel table = new TableInfoModel(model.getTableId(), model.getPotValue(), model.getRoomId(), model.getPaired() == 1, model.getGameServerId(), model.getTableUsers(), model.getBet());
                    tables.add(table);

                    System.out.println("Tables in getTablesInfo: " + NetworkMessage.getGson().toJson(tables));
                }
            }
        }

        tables = tables.stream().filter(x -> x.filterTable(filter)).collect(Collectors.toList());

        tables.sort((o1, o2) -> o2.getGamers().size() - o1.getGamers().size());

        System.out.println("Sorted tables in getTablesInfo: " + NetworkMessage.getGson().toJson(tables));

        return tables;
    }

    public ArrayList<TableInfoModel> getRoomTables(int roomId){

        ArrayList<TableInfoModel> tables = new ArrayList<>();

        TreeSet<ServiceTableModel> treeSet = orderedTables.get(roomId);
        for (ServiceTableModel model : treeSet) {
            int gameNode = getTableGameNode(model.getTableId());
            if(gameNode == -1) continue;


            if (model.getTableType().equals(TableType.PUBLIC)) {
                TableInfoModel table = new TableInfoModel(model.getTableId(),model.getPotValue(),model.getRoomId(),model.getPaired() == 1,model.getGameServerId(),model.getTableUsers(), model.getBet());
                tables.add(table);
            }
        }

        return  tables;
    }

    public boolean isLowBet(long money, long bet){
        int multiplier = ServerGlobalVariables.getInstance().getInt("LowBetMultiplier",60);
        int limit = ServerGlobalVariables.getInstance().getInt("LowBetLimit",4000);

        return (money >= (multiplier * bet *3)) && (bet < limit);
    }

    private List<TableInfoModel> getAvailableTables(ServiceUser user) {
        List<TableInfoModel> tables = new ArrayList<>();
        BasicUserModel userModel = user.getUser();
        for (RoomType roomType : roomTypes) {
            if (roomType.getMinBet() > userModel.getMoney() || (roomType.getVipMode() && !userModel.isVip()) || isLowBet(userModel.getMoney(), roomType.getBet())) {
                continue;
            }
            int roomId = roomType.getId();

            TreeSet<ServiceTableModel> treeSet = orderedTables.get(roomId);
            treeSet = treeSet.stream().filter(x -> x.getGamerCount() < x.getSideCount()).collect(toCollection(TreeSet::new));
            tables = getTablesInfoList(treeSet);
        }

        return tables;
    }

    private List<TableInfoModel> getTablesInfoList(TreeSet <ServiceTableModel> treeSet){
        List<TableInfoModel> tables = new ArrayList<>();
        for (ServiceTableModel model : treeSet) {
            if (model.getTableType().equals(TableType.PUBLIC)) {
                TableInfoModel table = new TableInfoModel(model.getTableId(),model.getPotValue(),model.getRoomId(),model.getPaired() == 1,model.getGameServerId(),model.getTableUsers(), model.getBet());
                tables.add(table);
            }
        }

        return tables;
    }

    public void updateUserRoom (BasicUserModel userModel,boolean add){
        if (userModel.getRoomId() > 0) {
            Room room = rooms.get(userModel.getRoomId());
            room.updateUserSet(userModel.getId(), add);

            refreshServiceUser(userModel);
            ServiceUser serviceUser = users.get(userModel.getId());

            NetworkMessage tableInfoMessage = new NetworkMessage(GameCommands.TABLES_INFO);

            int roomId = serviceUser.getUser().getRoomId();

            TreeSet<ServiceTableModel> treeSet = orderedTables.get(roomId);
            List tables = getTablesInfoList(treeSet);

            if(add) {
//                tableInfoMessage.setDataAsJSON(new TablesInfoResponse(tables, getRoomCount(roomId), roomId));
//                sendNetworkMessage(userModel.getId(), userModel.getProxyId(), tableInfoMessage);
            }

        }
    }

    public void fixUser(String userId){
        try {
            if(!users.containsKey(userId)) {
                BasicUserModel basicUser = DBController.getInstance().getBasicUser(userId);
                String platform = cacheController.getUserLastPlatform(userId);
                basicUser.setPlatform(platform);
                refreshServiceUser(basicUser);
            }
        }catch (Exception e){
            logger.info(e.getMessage());
        }
    }

    public void addUser(BasicUserModel userModel){

        refreshServiceUser(userModel);
//        ServiceUser serviceUser = users.get(userModel.getId());
//
//        NetworkMessage tableInfoMessage = new NetworkMessage(GameCommands.SET_TABLES_INFO);
//        tableInfoMessage.setData(sendAvailableTables(serviceUser));
//
//        sendNetworkMessage(userModel.getId(), userModel.getProxyId(), tableInfoMessage);
    }

    private void refreshServiceUser (BasicUserModel userModel){
        if (!users.containsKey(userModel.getId())) {
            users.put(userModel.getId(), new ServiceUser(userModel));
        } else {
            users.get(userModel.getId()).setUser(userModel);
        }
    }

    public void removeUser (String userId){
        ServiceUser serviceUser = users.get(userId);
        if (serviceUser != null) {
            users.remove(userId);
        }
    }

    public ServiceUser getUser (String id){
        return users.get(id);
    }

    public void initRooms () {
        ArrayList<RoomType> roomTypes = DBController.getInstance().getRoomTypes();
        createRooms(roomTypes);

        Timer timer = new Timer();
        TimerTask timerTask = new RefreshTablesTask();
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void createRooms (ArrayList < RoomType > roomTypes) {
        if (this.roomTypes != null) return;
        this.roomTypes = roomTypes;

        for (RoomType roomType : roomTypes) {
            roomType.setServerType(ServerType.GENERIC);
            Room room = new Room(roomType);
            this.rooms.put(roomType.getId(), room);
            this.orderedTables.put(roomType.getId(), new TreeSet<>());
        }
    }

    public RoomType getSmallestRoomType () {
        return this.roomTypes.get(0);
    }

    public RoomType getBiggestRoomType () {
        return this.roomTypes.get(this.roomTypes.size() - 1);
    }


    public AvailableTableModel getQuickTable (String userId, TableFilterModel filter){
        ServiceUser serviceUser = users.get(userId);

        long userMoney = serviceUser.getUser().getMoney();

        ArrayList<RoomType> checkRange = new ArrayList<>();

        int roomIdFilter = filter.getRoomId();

        if(roomIdFilter == -1) {
            int quickPlayRoomDown = 1;
            int quickPlayRange = 3;
            int bigRoom = getBiggestRoomType().getId();
            int smallRoom = getSmallestRoomType().getId();

            // add to list as quickPlayRange
            int rangeCounter = 0;
            for (int i = bigRoom; i >= smallRoom; i--) {
                Room room = rooms.get(i);
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
                checkRange.subList(0, quickPlayRoomDown).clear();
            }
        }else{
            Room room = rooms.get(roomIdFilter);
            RoomType roomType = room.getRealRoomType();

            if (userMoney >= roomType.getMinBet() && !isLowBet(userMoney, roomType.getBet())) {
                checkRange.add(roomType);
            }
        }

        AvailableTableModel available = null;
        for (int c = 3; c > 0; c--) {
            if(available != null) break;
            for (RoomType roomType : checkRange) {
                if(available != null) break;
                List<ServiceTableModel> filteredTables = orderedTables.get(roomType.getId()).stream().filter(x -> x.filterTable(filter)).collect(Collectors.toList());
                for (ServiceTableModel tableModel : filteredTables) {
                    int side = tableModel.getEmptySide();
                    if (side != -1 && isAvailableTable(tableModel, c, serviceUser)) {
                        serviceUser.addQuickTable(tableModel.getTableId());
                        available = new AvailableTableModel(tableModel.getGameServerId(), tableModel.getRoomId(), tableModel.getTableId(), side);
                        break;
                    }
                }
            }
        }

        return available;
    }

    public List<QuickPlayRoomModel> getQuickPlayRooms(){
        List<QuickPlayRoomModel> quickPlayRooms = new ArrayList<>();

        int bigRoom = getBiggestRoomType().getId();
        int smallRoom = getSmallestRoomType().getId();

        quickPlayRooms.add(new QuickPlayRoomModel("Tüm Odalar", -1, -1));

        for(int i = smallRoom ; i <= bigRoom ; i++){
            Room room = rooms.get(i);
            RoomType roomType = room.getRealRoomType();

            QuickPlayRoomModel quickPlayRoom = new QuickPlayRoomModel(roomType.getName(), roomType.getBet(), i);
            quickPlayRooms.add(quickPlayRoom);
        }

        return quickPlayRooms;
    }

    public Room getRoom(int roomId){
        return rooms.get(roomId);
    }

    private boolean isAvailableTable (ServiceTableModel tableModel,int count, ServiceUser user){
        return !user.isContainsTable(tableModel.getTableId()) && tableModel.isAvailableForQuickPlay(count);
    }

    public ArrayList<RoomUserCountModel> getRoomUserCount () {
        return roomUsersController.getUserCounts();
    }

    public int getTableGameNode ( int tableId){
        ServiceTableModel serviceTableModel = tables.get(tableId);
        return serviceTableModel == null ? -1 : serviceTableModel.getGameServerId();
    }

    private void resetTables() {
        tables.clear();
        orderedTables.clear();
        for (Integer roomId: rooms.keySet()) {
            rooms.get(roomId).resetTables();
            orderedTables.put(roomId, new TreeSet<>());
        }

        cacheController.publishTablesReset();
        logger.info("[TABLE RESET PUBLISH]");
    }

    public ArrayList<ServiceTableModel> getTables(int roomId) {
        Room room = rooms.get(roomId);

        ArrayList<ServiceTableModel> tableList = new ArrayList<>();
        ConcurrentHashSet<Integer> tableSet = room.getTableSet();
        for (Integer tableId : tableSet){
            ServiceTableModel serviceTableModel = tables.get(tableId);
            tableList.add(serviceTableModel);
        }

        return tableList;
    }

    public void addTableInfoListener(){
        cacheController.listenTablesInfoForUserEvent((charSequence, userId) -> {

        });
    }


    class RefreshTablesTask extends TimerTask{

        @Override
        public void run() {
            for (RoomType roomType : roomTypes){
                int roomId = roomType.getId();
                Room room = rooms.get(roomId);

                ConcurrentHashSet<Integer> roomTableSet = room.getTableSet();
                if (roomTableSet == null) return;

                TreeSet<ServiceTableModel> treeSet = new TreeSet<>();
                int emptySingleTableCount = 0;
                int emptyPairedTableCount = 0;
                int fullSingleTableCount = 0;
                int fullPairedTableCount = 0;

                int allFullSingleTableCount = 0;
                int allFullPairedTableCount = 0;
                int allEmptySingleTableCount = 0;
                int allEmptyPairedTableCount = 0;

                int halfPairedTableLimit = ServerGlobalVariables.getInstance().getInt("halfPairedTableLimit", 6);
                int halfSingleTableLimit = ServerGlobalVariables.getInstance().getInt("halfSingleTableLimit", 6);

                int halfPairedTableCount = 0;
                int halfSingleTableCount = 0;

                for (int tableId : roomTableSet) {
                    ServiceTableModel serviceTable = tables.get(tableId);
                    if (serviceTable != null) {
                        if (serviceTable.getGamerCount() == serviceTable.getSideCount() && !serviceTable.isPaired() && fullSingleTableCount < 20) {
                            treeSet.add(serviceTable);

                            if(allFullSingleTableCount < 4){
                                serviceTable.setFullnessAllAvailable(true);
                                allFullSingleTableCount++;
                            }else{
                                serviceTable.setFullnessAllAvailable(false);
                            }

                            fullSingleTableCount++;
                        } else if (serviceTable.getGamerCount() == serviceTable.getSideCount() && serviceTable.isPaired() && fullPairedTableCount < 20) {
                            treeSet.add(serviceTable);

                            if(allFullPairedTableCount < 4){
                                serviceTable.setFullnessAllAvailable(true);
                                allFullPairedTableCount++;
                            }else{
                                serviceTable.setFullnessAllAvailable(false);
                            }

                            fullPairedTableCount++;
                        } else if (serviceTable.getGamerCount() == 0 && !serviceTable.isPaired() && emptySingleTableCount < 20) {
                            treeSet.add(serviceTable);

                            if(allEmptySingleTableCount < 4){
                                serviceTable.setFullnessAllAvailable(true);
                                allEmptySingleTableCount++;
                            }else{
                                serviceTable.setFullnessAllAvailable(false);
                            }

                            emptySingleTableCount++;
                        } else if (serviceTable.getGamerCount() == 0 && serviceTable.isPaired() && emptyPairedTableCount < 20) {
                            treeSet.add(serviceTable);

                            if(allEmptyPairedTableCount < 4){
                                serviceTable.setFullnessAllAvailable(true);
                                allEmptyPairedTableCount++;
                            }else{
                                serviceTable.setFullnessAllAvailable(false);
                            }

                            emptyPairedTableCount++;
                        }

                        else if (serviceTable.getGamerCount() < 4 && serviceTable.getGamerCount() > 0 && serviceTable.isPaired() && halfPairedTableCount < halfPairedTableLimit) {
                            treeSet.add(serviceTable);
                            serviceTable.setFullnessAllAvailable(true);
                            halfPairedTableCount++;
                        }
                        else if (serviceTable.getGamerCount() < 4 && serviceTable.getGamerCount() > 0 && !serviceTable.isPaired() && halfSingleTableCount < halfSingleTableLimit) {
                            treeSet.add(serviceTable);
                            serviceTable.setFullnessAllAvailable(true);
                            halfSingleTableCount++;
                        }
                    }
                }
                orderedTables.put(roomId, treeSet);
            }
        }
    }

    public int getRoomCount(int roomId){
        if(roomId == -1) return 0;
        return roomUsersController.getRoomUserCount(roomId);
    }
}
