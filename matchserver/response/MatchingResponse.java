package com.mynet.matchserver.response;

import com.mynet.shared.model.BasicUserModel;

public class MatchingResponse {
    private final boolean lookingForOp;
    private final int tableId;
    private final int roomId = 1; // unnecessary field
    private final BasicUserModel[] users;
    private final int side;
    private final int sideCount;
    private final int tournamentId;
    private final int gameNodeId;


    private MatchingResponse(int tableId, int side, int sideCount, boolean lookingForOp, int tournamentId, BasicUserModel[] users, int gameNode) {
        this.tableId = tableId;
        this.side = side;
        this.sideCount = sideCount;
        this.lookingForOp = lookingForOp;
        this.tournamentId = tournamentId;
        this.users = users;
        this.gameNodeId = gameNode;
    }

    private MatchingResponse(int sideCount) {
        this(0, 0, sideCount, true, -1, null, -1);
    }

    public boolean isLookingForOp() {
        return lookingForOp;
    }

    public int getTableId() {
        return tableId;
    }

    public int getRoomId() {
        return roomId;
    }

    public BasicUserModel[] getUsers() {
        return users;
    }

    public int getSide() {
        return side;
    }

    public int getSideCount() {
        return sideCount;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public int getGameNodeId(){ return gameNodeId; };

    public static class Builder{
        private boolean lookingForOp;
        private int tableId;
        private int side;
        private int sideCount;
        private int tournamentId;
        private BasicUserModel[] users;
        private int gameNodeId;

        public Builder lookingForOp(final boolean lookingForOp){
            this.lookingForOp = lookingForOp;
            return this;
        }

        public Builder tableId(final int tableId){
            this.tableId = tableId;
            return this;
        }

        public Builder side(final int side){
            this.side = side;
            return this;
        }

        public Builder sideCount(final int sideCount){
            this.sideCount = sideCount;
            return this;
        }

        public Builder tournamentId(final int tournamentId){
            this.tournamentId = tournamentId;
            return this;
        }

        public Builder users(BasicUserModel[] users){
            this.users = users;
            return this;
        }

        public Builder gameNodeId(int gamNode){
            this.gameNodeId =gamNode;
            return this;
        }

        public MatchingResponse build(){
            return new MatchingResponse(tableId, side, sideCount, lookingForOp, tournamentId, users, gameNodeId);
        }


        public MatchingResponse buildInitial(){
            return new MatchingResponse(sideCount);
        }
    }
}
