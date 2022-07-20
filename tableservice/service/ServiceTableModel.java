package com.mynet.tableservice.service;

import com.mynet.gameserver.enums.TableType;
import com.mynet.gameserver.model.TableFilterModel;
import com.mynet.gameserver.model.TableUserModel;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.tableservice.enums.TablePairedFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class  ServiceTableModel implements Comparable<ServiceTableModel> {
    private static Logger logger = LoggerFactory.getLogger(ServiceTableModel.class);
    private int tableId;
    private int roomId;
    private int gameServerId;
    private int bet;
    private int minBet;
    private long potValue;
    private boolean isVip;
    private int sideCount;
    private int dealerSide;
    private int paired;
    private TableType tableType;
    private ConcurrentHashMap<Integer, ServiceTableSideModel> sides;
    private int gamerCount;
    private int botCount;
    private boolean fullnessAllAvailable;

    public ServiceTableModel() {
        this.dealerSide = -1;
        this.sideCount = 4;
        this.potValue = 0;
        this.gamerCount = 0;
        this.botCount = 0;
        this.sides = new ConcurrentHashMap<>(sideCount);
        for (int side = 0; side < this.sideCount; side++) {
            this.sides.put(side, new ServiceTableSideModel(side, null, false));
        }
    }

    public void setSide(int side, BasicUserModel user){
        ServiceTableSideModel serviceTableSideModel = sides.get(side);
        if(serviceTableSideModel == null){
            serviceTableSideModel = new ServiceTableSideModel(side, user, false);
            sides.put(side, serviceTableSideModel);
        }else {
            serviceTableSideModel.setUser(user);
            serviceTableSideModel.setBot(false);
        }

        refreshCounts();
    }

    private void refreshCounts() {
        int userCount = 0;
        int bots = 0;
        for (ServiceTableSideModel sideModel: sides.values()){
            if(sideModel.getUser() != null){
                userCount++;
            }else if(sideModel.isBot()){
                bots++;
            }
        }

        this.gamerCount = userCount;
        this.botCount = bots;
    }

    public void setSide(int side, boolean bot){
        ServiceTableSideModel serviceTableSideModel = sides.get(side);
        if(serviceTableSideModel == null){
            serviceTableSideModel = new ServiceTableSideModel(side, null, bot);
            sides.put(side, serviceTableSideModel);
        }else{
            serviceTableSideModel.setBot(bot);
            serviceTableSideModel.setUser(null);
        }

        logger.warn("SIDE CLEARED!");

        refreshCounts();
    }

    public boolean isFullnessAllAvailable() {
        return fullnessAllAvailable;
    }

    public void setFullnessAllAvailable(boolean fullnessAllAvailable) {
        this.fullnessAllAvailable = fullnessAllAvailable;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public void setMinBet(int minBet) {
        this.minBet = minBet;
    }

    public void setPotValue(long potValue) {
        this.potValue = potValue;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public void setSideCount(int sideCount) {
        this.sideCount = sideCount;
    }

    public void setDealerSide(int dealerSide) {
        this.dealerSide = dealerSide;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    public void setSides(ConcurrentHashMap<Integer, ServiceTableSideModel> sides) {
        this.sides = sides;
    }

    public int getTableId() {
        return tableId;
    }

    public int getBet() {
        return bet;
    }

    public int getMinBet() {
        return minBet;
    }

    public long getPotValue() {
        return potValue;
    }

    public int getRoomId() {
        return roomId;
    }

    public boolean isVip() {
        return isVip;
    }

    public int getSideCount() {
        return sideCount;
    }

    public int getDealerSide() {
        return dealerSide;
    }

    public TableType getTableType() {
        return tableType;
    }

    public int getPaired() {
        return paired;
    }

    public boolean isPaired(){
        return paired == 1;
    }

    public void setPaired(int paired) {
        this.paired = paired;
    }

    public ConcurrentHashMap<Integer, ServiceTableSideModel> getSides() {
        return sides;
    }

    public int getGameServerId() {
        return gameServerId;
    }

    public void setGameServerId(int gameServerId) {
        this.gameServerId = gameServerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceTableModel model = (ServiceTableModel) o;
        return tableId == model.tableId &&
                roomId == model.roomId &&
                gameServerId == model.gameServerId;
    }

    public int getGamerCount() {
        return this.gamerCount;
    }

    public boolean isAvailableForQuickPlay(int count){
        return tableType.equals(TableType.PUBLIC) && gamerCount == count && botCount == 0;
    }

    public int getBotCount() {
        return botCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableId, roomId, gameServerId);
    }

    @Override
    public int compareTo(ServiceTableModel o) {
        int uc1 = getTableOrderForSorting(this);
        int uc2 = getTableOrderForSorting(o);

        return uc2 > uc1 ? 1 : -1;
    }

    private int getTableOrderForSorting(ServiceTableModel tableModel) {
        int uc = tableModel.gamerCount;
        uc += 2;
        if (uc == 6) { // full
            if (tableModel.botCount > 0) {
                uc = 2; // full with bots
            } else {
                uc = 0; // full
            }
        } else if (uc == 2) { // empty
            uc = 1;
        }
        return uc;
    }

    public String getGamersIdsNames() {
        StringBuilder table_gamers = new StringBuilder();
        for (int i = 0; i < sideCount; i++) {
            ServiceTableSideModel sideModel = this.sides.get(i);

            String fuid = "";
            try {
                if (sideModel != null) {

                    BasicUserModel sideUser = sideModel.getUser();
                    boolean bot = sideModel.isBot();

                    if (bot) {
                        fuid = "Robot" + "#" + (100 + sideModel.getSide()) + "#" + 0;
                    } else if (sideUser != null) {
                        int userVip = sideUser.isVip() ? 1 : 0;
                        fuid = sideUser.getName() + "#" +  sideUser.getId() + "#" + userVip;
                    }

                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            if (i < 3) {
                table_gamers.append(fuid).append("|");
            } else {
                table_gamers.append(fuid);
            }

        }
        return table_gamers.toString();
    }

    public List<TableUserModel> getTableUsers() {
        List<TableUserModel> gamers =  new ArrayList<>();

        for (int i = 0; i < sideCount; i++) {
            ServiceTableSideModel sideModel = this.sides.get(i);

            try {
                if (sideModel != null) {
                    boolean bot = sideModel.isBot();

                    String fuid = "";
                    String name = "";
                    String platform = "web";
                    long money = 0;
                    boolean isVip = false;

                    if(bot){
                        name = "Robot";
                        fuid = String.valueOf(100 + sideModel.getSide());

                        TableUserModel gamer = new TableUserModel(bot,isVip,i,name,fuid, null, null, platform, money, true, false);
                        gamers.add(gamer);

                    }else{
                        BasicUserModel sideUser = sideModel.getUser();

                        if(sideUser != null) {
                            name = sideUser.getName();
                            fuid = sideUser.getId();
                            isVip = sideUser.isVip();
                            platform = sideUser.getPlatform();

                            TableUserModel gamer = new TableUserModel(bot,isVip,i,name,fuid, null, null, platform, money, true, false);
                            gamers.add(gamer);
                        }

                    }

                }else{
                    gamers.add(null);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
        return gamers;
    }

    public int getEmptySide() {
        for (ServiceTableSideModel sideModel: sides.values()){
            if(sideModel.isEmpty()){
                return sideModel.getSide();
            }
        }

        return -1;
    }

    public void setGamerCount(int gamerCount) {
        this.gamerCount = gamerCount;
    }

    public void setBotCount(int botCount) {
        this.botCount = botCount;
    }

    public boolean filterTable(TableFilterModel filter){
        TablePairedFilter pairedFilter = filter.getPaired();
        return (pairedFilter.equals(TablePairedFilter.ALL) || (isPaired() && pairedFilter.equals(TablePairedFilter.PAIRED)) || (!isPaired() && pairedFilter.equals(TablePairedFilter.NOT_PAIRED)));
    }

    @Override
    public String toString() {
        StringBuilder sideBuilder = new StringBuilder();
        for (ServiceTableSideModel model : sides.values()){
            sideBuilder.append("side: ").append(model.getSide());
            if(model.isEmpty()){
                sideBuilder.append(" is empty | ");
            }else if(model.isBot()){
                sideBuilder.append(" is bot | ");
            }else{
                sideBuilder.append(" is ").append(model.getUser().getId());
            }
        }

        return "ServiceTableModel{" +
                "tableId=" + tableId +
                ", gameServerId=" + gameServerId +
                ", minBet=" + minBet +
                ", potValue=" + potValue +
                ", tableType=" + tableType +
                ", sides=" + sideBuilder.toString() +
                '}';
    }
}
