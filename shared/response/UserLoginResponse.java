package com.mynet.shared.response;

import com.mynet.shared.types.ServerType;

import java.util.List;

public class UserLoginResponse {
    private int serverType;
    private List<ClientTournamentInfoResponse> tournamentList;
    private long tickets;
    private long userMoney;
    private boolean isVip;
    private boolean reconnect;
    private boolean privateChat;
    private boolean goProfile;

    public UserLoginResponse(Builder builder) {
        this.serverType = builder.serverType.getValue();
        this.tournamentList = builder.tournamentList;
        this.tickets = builder.tickets;
        this.userMoney = builder.userMoney;
        this.isVip = builder.isVip;
        this.reconnect = builder.reconnect;
        this.privateChat = builder.privateChat;
        this.goProfile = builder.goProfile;
    }

    @Override
    public String toString() {
        return "UserLoginResponse{" +
                "serverType=" + serverType +
                ", tournamentList=" + tournamentList +
                ", tickets=" + tickets +
                ", userMoney=" + userMoney +
                ", isVip=" + isVip +
                '}';
    }

    public static class Builder {

        public boolean privateChat;
        public boolean goProfile;
        private ServerType serverType;
        private List<ClientTournamentInfoResponse> tournamentList;
        private long tickets;
        private long userMoney;
        private boolean isVip;
        private boolean reconnect;

        public Builder setServerType(ServerType serverType) {
            this.serverType = serverType;
            return this;
        }

        public Builder setTournamentList(List<ClientTournamentInfoResponse> tournamentList) {
            this.tournamentList = tournamentList;
            return this;
        }

        public Builder setTickets(long tickets) {
            this.tickets = tickets;
            return this;
        }

        public Builder setUserMoney(long userMoney){
            this.userMoney = userMoney;
            return this;
        }

        public Builder setIsVip(boolean isVip){
            this.isVip = isVip;
            return this;
        }

        public Builder setReconnect(boolean reconnect) {
            this.reconnect = reconnect;
            return this;
        }

        public Builder setPrivateChat(boolean privateChat) {
            this.privateChat = privateChat;
            return this;
        }

        public Builder setGoProfile(boolean goProfile) {
            this.goProfile = goProfile;
            return this;
        }

        public UserLoginResponse build() {
            return new UserLoginResponse(this);
        }
    }
}
