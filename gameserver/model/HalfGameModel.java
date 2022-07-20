package com.mynet.gameserver.model;

import com.mynet.gameserver.okey.CardHandler;
import com.mynet.gameserver.okey.TableSideStateModel;
import com.mynet.gameserver.okey.TableTimerModel;

public class HalfGameModel {
    private int tableId;
    private long pot;
    private long[] users;
    private int[] doubles;
    private int paired;
    private boolean privateTable;
    private int roomId;
    private int dealerSide;
    private int sideCount;
    private int state;
    private CardHandler cardHandler;
    private TableTimerModel tableTimerModel;
    private long gameId;
    private boolean isFirstTurn;
    private TableSideStateModel sideState;

    private HalfGameModel(int tableId, int roomId, int paired, int isPrivate, long pot, long side0, long side1, long side2, long side3, int dealerSide, int state, CardHandler cardHandler, TableTimerModel model, long gameId, boolean isFirstTurn, TableSideStateModel sideState, int[] doubles) {
        this.tableId = tableId;
        this.pot = pot;
        this.roomId = roomId;
        this.paired = paired;
        this.privateTable = isPrivate == 1;
        this.users = new long[4];
        this.users[0] = side0;
        this.users[1] = side1;
        this.users[2] = side2;
        this.users[3] = side3;
        this.dealerSide = dealerSide;
        this.sideCount = 4;
        this.state = state;
        this.cardHandler = cardHandler;
        this.tableTimerModel = model;
        this.isFirstTurn = isFirstTurn;
        this.gameId = gameId;
        this.sideState = sideState;
        this.doubles = doubles;
    }

    public HalfGameModel() {
    }

    public int getTableId() {
        return tableId;
    }

    public long getPot() {
        return pot;
    }

    public long[] getUsers() {
        return users;
    }

    public int getPaired() {
        return paired;
    }

    public boolean isPrivateTable() {
        return privateTable;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getSideCount() {
        return sideCount;
    }

    public int getDealerSide() {
        return dealerSide;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public void setPot(long pot) {
        this.pot = pot;
    }

    public void setUsers(long[] users) {
        this.users = users;
    }

    public void setPaired(int paired) {
        this.paired = paired;
    }

    public void setPrivateTable(boolean privateTable) {
        this.privateTable = privateTable;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setDealerSide(int dealerSide) {
        this.dealerSide = dealerSide;
    }

    public void setSideCount(int sideCount) {
        this.sideCount = sideCount;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public CardHandler getCardHandler() {
        return cardHandler;
    }

    public void setCardHandler(CardHandler cardHandler) {
        this.cardHandler = cardHandler;
    }

    public TableTimerModel getTableTimerModel() {
        return tableTimerModel;
    }

    public void setTableTimerModel(TableTimerModel tableTimerModel) {
        this.tableTimerModel = tableTimerModel;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public boolean isFirstTurn() {
        return isFirstTurn;
    }

    public void setFirstTurn(boolean firstTurn) {
        isFirstTurn = firstTurn;
    }

    public TableSideStateModel getSideState() {
        return sideState;
    }

    public void setSideState(TableSideStateModel sideState) {
        this.sideState = sideState;
    }

    public int[] getDoubles() {
        return doubles;
    }

    public void setDoubles(int[] doubles) {
        this.doubles = doubles;
    }

    public static class Builder {
        private int tableId;
        private long pot;
        private long[] users;
        private int[] doubles;
        private int paired;
        private boolean privateTable;
        private int roomId;
        private int dealerSide;
        private int state;
        private CardHandler cardHandler;
        private TableTimerModel tableTimerModel;
        private long gameId;
        private boolean isFirstTurn;
        private TableSideStateModel sideStateModel;

        public Builder tableId(int id){
            this.tableId = id;
            return this;
        }

        public Builder pot(long id){
            this.pot = id;
            return this;
        }

        public Builder paired(int value){
            this.paired = value;
            return this;
        }

        public Builder privateTable(boolean value){
            this.privateTable = value;
            return this;
        }

        public Builder roomId(int id){
            this.roomId = id;
            return this;
        }

        public Builder dealerSide(int side){
            this.dealerSide = side;
            return this;
        }

        public Builder users(long[] users){
            this.users = users;
            return this;
        }

        public Builder doubles(int[] doubles){
            this.doubles = doubles;
            return this;
        }

        public Builder state(int st){
            this.state = st;
            return this;
        }

        public Builder cardHandler(CardHandler ch){
            this.cardHandler = ch;
            return this;
        }

        public Builder tableTimerModel(TableTimerModel ttm){
            this.tableTimerModel = ttm;
            return this;
        }

        public Builder isFirstTurn(boolean isFirst){
            this.isFirstTurn = isFirst;
            return this;
        }

        public Builder gameId(long gameId){
            this.gameId = gameId;
            return this;
        }

        public Builder sideStateModel(TableSideStateModel sideModel){
            this.sideStateModel = sideModel;
            return this;
        }

        public HalfGameModel build(){
            return new HalfGameModel(tableId, roomId, paired, privateTable ? 1: 0, pot, users[0], users[1], users[2], users[3], dealerSide, state, cardHandler, tableTimerModel, gameId, isFirstTurn, sideStateModel, doubles);
        }
    }
}
