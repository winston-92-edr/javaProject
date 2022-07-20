package com.mynet.shared.logs;

public class GameUserThreadLog extends QueueElement{
    private final String id;
    private final String name;
    private final String platform;
    private final long money;
    private final boolean isVip;
    private final long ticket;

    private final int tournamentId;
    private final int proxyId;
    private final int roomId;

    public GameUserThreadLog(Builder b) {
        this.id = b.id;
        this.name = b.name;
        this.platform = b.platform;
        this.money = b.money;
        this.isVip = b.isVip;
        this.ticket = b.ticket;
        this.tournamentId = b.tournamentId;
        this.proxyId = b.proxyId;
        this.roomId = b.roomId;
    }

    public static class Builder{
        private String id;
        private String name;
        private String platform;
        private long money;
        private boolean isVip;
        private long ticket;

        private int tournamentId;
        private int proxyId;
        private int roomId;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        public Builder setMoney(long money) {
            this.money = money;
            return this;
        }

        public Builder setVip(boolean vip) {
            isVip = vip;
            return this;
        }

        public Builder setTicket(long ticket) {
            this.ticket = ticket;
            return this;
        }

        public Builder setTournamentId(int tournamentId) {
            this.tournamentId = tournamentId;
            return this;
        }

        public Builder setProxyId(int proxyId) {
            this.proxyId = proxyId;
            return this;
        }

        public Builder setRoomId(int roomId) {
            this.roomId = roomId;
            return this;
        }

        public GameUserThreadLog build(){
            return new GameUserThreadLog(this);
        }
    }
}
