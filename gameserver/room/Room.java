package com.mynet.gameserver.room;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.response.GetRoomTablesResponse;
import com.mynet.matchserver.GameUser;
import com.mynet.proxyserver.network.StringUtil;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.types.GamePlayStatusType;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.jooq.util.derby.sys.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Room {
    private static final Logger logger = LoggerFactory.getLogger(Room.class);

    private ConcurrentHashMap<String, GameUser> users = new ConcurrentHashMap<>();
    private RoomType roomType;

    private int tablesCount = 0;
    private int gamePlay=0;
    private int gameMoney=0;
    private GameController gameController;

    private Hashtable<Integer, Table> tables = new Hashtable<>();

    //this fields only use in table service
    private ConcurrentHashSet<Integer> tableSet;
    private HashSet<String> userSet;


    public Room(RoomType roomType, int tablesCount, GameController gameController)
    {
        this.roomType = roomType;
        this.tablesCount = tablesCount;
        this.gameController = gameController;
    }

    public Room(RoomType roomType)
    {
        this.roomType = roomType;
        this.tableSet = new ConcurrentHashSet<>();
        this.userSet = new HashSet<>();
    }

    public ConcurrentHashSet<Integer> getTableSet() {
        return tableSet;
    }

    public void resetTables(){
        tableSet.clear();
    }

    public synchronized void AddTable(int tableId, Table table)
    {
        tables.put(tableId, table);
    }

    public Table getTableWithId(int tableId){
        return tables.get(tableId);
    }

    public synchronized void removeTable(Table table)
    {
        try {
            if(tables.containsKey(table.getTableId())){
                tables.remove(table.getTableId());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public synchronized Enumeration<Table> getTables()
    {
        return this.tables.elements();
    }

    public int getTablesCount()
    {
        return this.tablesCount;
    }

    public void AddUser(GameUser user)
    {

        if (!users.containsKey(user.getfuid()))
        {
            users.put(user.getfuid(), user);
        }

        user.setRoomId(this.roomType.getId());

        if(gameController != null){
            gameController.addUserTableServiceRooms(user.getBasicUser());
        }

        CacheController.getInstance().setUserRoomID(this.roomType.getId(), user.getId());

    }

    public ErrorCode addUserSecure(GameUser user) {
        ErrorCode errorCode = null;
        if (user != null) {
            if (!user.hasRoom()) {
                GamePlayStatusType status = canAddUserSecure(user);
                if (status == GamePlayStatusType.VALID) {
                    AddUser(user);
                }
            } else {
                errorCode = ErrorCode.USER_HAS_ROOM_ALREADY;
            }
        } else {
            logger.error("addUserSecure user is null ");
            errorCode = ErrorCode.GENERAL_ERROR;
        }
        return errorCode;
    }

    public GamePlayStatusType canAddUserSecure(GameUser user) {
        GamePlayStatusType error = GamePlayStatusType.VALID;
        if (user != null) {
            if (isVip() && !user.getIsVip()) {
                error = GamePlayStatusType.NOT_VIP;
            } else if (user.getMoney() < getMinBet()) {
                error = GamePlayStatusType.NOT_ENOUGH_MONEY;
            }
        } else {
            logger.error("canAddUserSecure user is null");
            error = GamePlayStatusType.GENERAL_ERROR;
        }
        return error;
    }


    public void RemoveUser(GameUser user)
    {
        users.remove(user.getfuid());

        if(gameController != null){
            gameController.removeUserTableServiceRooms(user.getBasicUser());
        }

        user.setRoomId(0);
        CacheController.getInstance().setUserRoomID(-1, user.getId());

    }


    public void setGamePlayCount ()
    {
        this.gamePlay++;
    }

    public void resetGamePlayCount ()
    {
        this.gamePlay=0;
    }

    public int getGamePlayCount()
    {
        return this.gamePlay;
    }

    public void setGameMoneyCount (int money)
    {
        this.gameMoney=this.gameMoney+money;
    }

    public void resetGameMoneyCount ()
    {
        this.gameMoney=0;
    }

    public int getGameMoneyCount()
    {
        return this.gameMoney;
    }


    public Enumeration<GameUser> getAllUsers()
    {
        return users.elements();
    }

    public boolean hasUser(String fuid) {
        return users.containsKey(fuid);
    }

    public int getUsersCount() {
        if(gameController != null){
            return users.size();
        }
        return userSet.size();
    }

    public String getUsersInRoomLobby() {
        StringBuilder usersInfo = new StringBuilder();
        Enumeration<GameUser> en = users.elements();
        GameUser user;
        while (en.hasMoreElements()) {
            user = en.nextElement();
            if (user.getTableId() <= 0) {
                if (usersInfo.length() > 0) {
                    usersInfo.append(";");
                }
                usersInfo.append(user.getfuid()).append("|")
                        .append(user.getName()).append("|")
                        .append(user.getName()).append("|")
                        .append("n").append("|")
                        .append(1).append("|")
                        .append(user.getMoney());
            }
        }
        return StringUtil.correctTurkish(usersInfo.toString());
    }

    public String getUsersInRoom() {
        StringBuilder usersInfo = new StringBuilder();
        StringBuilder usersInfoVip = new StringBuilder();
        String usersInfoElite = "";

        for (Iterator<Map.Entry<String, GameUser>> it = users.entrySet().iterator(); it.hasNext() ;)  {
            GameUser us = it.next().getValue();

             if (us.getIsVip()) {

                if (usersInfoVip.length() > 0) {
                    usersInfoVip.append(";");
                }
                usersInfoVip.append(us.getfuid()).append("|")
                        .append(us.getName()).append("|")
                        .append(us.getName()).append("|")
                        .append("n").append("|")
                        .append(1).append("|")
                        .append(us.getMoney()).append("|")
                        .append(us.getIsVipAsInt()).append("|")
                        .append(0).append("|")
                        .append(0);

            } else {

                if (usersInfo.length() > 0) {
                    usersInfo.append(";");
                }
                usersInfo.append(us.getfuid()).append("|")
                        .append(us.getName()).append("|")
                        .append(us.getName()).append("|")
                        .append("n").append("|")
                        .append(1).append("|")
                        .append(us.getMoney()).append("|")
                        .append(us.getIsVipAsInt()).append("|")
                        .append(0).append("|")
                        .append(0);
            }
        }
        if( usersInfoVip.length() > 0 )
            usersInfoVip.append(";");
        usersInfoVip.append(usersInfo);

        usersInfoElite+=usersInfoVip;

        return StringUtil.correctTurkish(usersInfoElite);
    }

    public String getTableUsersCount()
    {
        StringBuilder info = new StringBuilder();

        Enumeration<Integer> tableIds = tables.keys();
        while (tableIds.hasMoreElements())
        {
            long tableId = tableIds.nextElement();
            if(tables.get(tableId).isDynamical()) continue;
            if (info.length() > 0)
            {
                info.append(";");
            }
            info.append(tableId).append(",")
                    .append(tables.get(tableId).getUserCount()).append(",")
                    .append(tables.get(tableId).getBotCount());

        }
        return info.toString();
    }


    public String getTablesInfo()
    {
        StringBuilder info = new StringBuilder();

        ArrayList<Table> vals = new ArrayList<>(tables.values());
        vals.sort((table1, table2) -> {
            int uc1 = getTableOrderForSorting(table1);
            int uc2 = getTableOrderForSorting(table2);

            return Integer.compare(uc2, uc1);
        });

        int fullTable = 0;
        int emptyTable = 0;

        for (Table table : vals) {
            if (!table.isVisibleInList()) {
                continue;
            }
            long tableId = table.getTableId();

            if (table.getUserCount() == 4) {
                fullTable++;
                /* Integer.parseInt((String)Prop.getValue("showFullTableLimit"*/
                if (fullTable >= 24) {
                    if (emptyTable > 8) {
                        break;
                    }
                    continue;
                }
            } else if (table.getUserCount() == 0) {
                emptyTable++;
            }

            if (info.length() > 0) {
                info.append(";");
            }

            info.append(tableId).append(",")
                    .append(table.getGamersFuidsNamesString()).append(",")
                    .append(table.getPotValue()).append(",")
                    .append(table.getRoomId()).append(",")
                    .append(table.getIsPartner());//+","+closedTable
        }

        return info.toString();
    }

    // order  3 players, 2 players, 1 players, 4 players with bots, empty, 4 players tables
    private int getTableOrderForSorting(Table table) {
        int uc = table.getUserCount();
        uc += 2;
        if (uc == 6) { // full
            if (table.getBotCount() > 0) {
                uc = 2; // full with bots
            } else {
                uc = 0; // full
            }
        } else if (uc == 2) { // empty
            uc = 1;
        }
        return uc;
    }

    public String getTablesInfoNotDinamic(){
        StringBuilder info = new StringBuilder();

        Enumeration<Integer> tableIds = tables.keys();

        while (tableIds.hasMoreElements())
        {
            long tableId = tableIds.nextElement();
            Table table = tables.get(tableId);
            if(table.isDynamical()) continue;
            if (info.length() > 0)
            {
                info.append(";");
            }
            info
                    .append(tableId).append(",")
                    .append(table.getGamersFuidsString()).append(",")
                    .append(table.getPotValue()).append(",")
                    .append(table.getRoomId()).append(",")
                    .append(table.getIsPartner());//+","+closedTable
        }

        return info.toString();
    }

    public long getPlayNow(int playerCount, GameUser usr)
    {
        Enumeration<Integer> tableIds = tables.keys();
        int tableId;

        while (tableIds.hasMoreElements())
        {
            tableId = tableIds.nextElement();

            if ( tables.get(tableId).getUserCount() == playerCount && !usr.isInLastTableIds(tableId))
                return tableId;
        }
        return -1;
    }

    public void cleanZombies()
    {
        Enumeration<GameUser> en = users.elements();
        while (en.hasMoreElements())
        {
            GameUser us = en.nextElement();
            if (!GameController.getInstance().isUserExist(us.getId()))
            {
                try
                {
                    Enumeration<Integer> tableIds = tables.keys();
                    while (tableIds.hasMoreElements())
                    {
                        long tableId = tableIds.nextElement();
                        Table t = tables.get(tableId);
                    }
                }
                catch(Exception ex)
                {
                    logger.error(ex.getMessage(), ex);
                }
                users.remove(us.getfuid());
            }
        }
    }

    public int getPlayNowQp(int playerCount)
    {
        Enumeration<Integer> tableIds = tables.keys();
        while (tableIds.hasMoreElements())
        {
            int tableId = tableIds.nextElement();


            if (tables.get(tableId).isVisibleForQuickPlay()
                    && tables.get(tableId).getUserCount() == playerCount
                    && tables.get(tableId).getGameStatus() == GameStatus.NOTSTARTED)
                return tableId;
        }
        return -1;
    }

    public RoomType getRealRoomType(){
        return roomType;
    }

    public boolean isVip() {
        return roomType.getVipMode();
    }

    public int getBet(){
        return roomType.getBet();
    }

    public int getMinBet(){
        return roomType.getMinBet();
    }

    public RoomType getRoomType(){
        return roomType;
    }

    public void updateUserSet(String userId, boolean add){
        if(add){
            userSet.add(userId);
        }else{
            userSet.remove(userId);
        }
    }

    public boolean isLowBet(long money){
        int multiplier = ServerGlobalVariables.getInstance().getInt("LowBetMultiplier",60);
        int limit = ServerGlobalVariables.getInstance().getInt("LowBetLimit",2500000);

        return GameController.getInstance().isGeneric() && (money >= (multiplier * getBet() *3)) && (getBet() < limit);
    }

    public NetworkMessage getRoomTablesMessage(boolean lock){
        NetworkMessage response = new NetworkMessage(GameCommands.GET_ROOM_TABLES);
        GetRoomTablesResponse getRoomTablesResponse = new GetRoomTablesResponse(lock,getTablesInfo());
        response.setSuccess(true);
        response.setDataAsJSON(getRoomTablesResponse);
        return response;
    }

    public long getAvailableTable(int playerCount, int partner) {
        Enumeration<Integer> tableIds = tables.keys();
        while (tableIds.hasMoreElements()) {
            long tableId = tableIds.nextElement();
            Table t = tables.get(tableId);

            if (t.isVisibleForQuickPlay()
                    && t.getUserCount() == playerCount
                    && t.getGameStatus() == GameStatus.NOTSTARTED
                    && t.getIsPartner() == partner)
                return tableId;
        }
        return -1;
    }
}
