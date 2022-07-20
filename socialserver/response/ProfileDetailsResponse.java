package com.mynet.socialserver.response;
import com.mynet.socialserver.enums.FriendStatus;

public class ProfileDetailsResponse {
    private String id;
    private int isOnline;
    private int gamesWon;
    private int gamesTotal;
    private long gamesPot;
    private long potMax;
    private int gamesLost;
    private long money;
    private boolean vip;
    private String currentGift;
    private boolean banned = false;
    private String banDate;
    private boolean muted = false;
    private FriendStatus friendStatus;
    private String muteDate;
    private String name;
    private String firstName;
    private String lastName;
    private String joinDate;
    private String tournamentBadgeStr;
    private boolean goProfile;
    private int friendsCount;

    public ProfileDetailsResponse(Builder builder) {
        this.id = builder.id;
        this.isOnline = builder.isOnline;
        this.gamesWon = builder.gamesWon;
        this.gamesTotal = builder.gamesTotal;
        this.gamesPot = builder.gamesPot;
        this.potMax = builder.potMax;
        this.gamesLost = builder.gamesLost;
        this.money = builder.money;
        this.vip = builder.vip;
        this.currentGift = builder.currentGift;
        this.banned = builder.banned;
        this.banDate = builder.banDate;
        this.muted = builder.muted;
        this.muteDate = builder.muteDate;
        this.name = builder.name;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.joinDate = builder.joinDate;
        this.tournamentBadgeStr = builder.tournamentBadgeStr;
        this.friendStatus = builder.friendStatus;
        this.goProfile = builder.goProfile;
        this.friendsCount = builder.friendsCount;
    }

    public static class Builder {
        private String id;
        private int isOnline;
        private int gamesWon;
        private int gamesTotal;
        private long gamesPot;
        private long potMax;
        private int gamesLost;
        private long money;
        private boolean vip;
        private String currentGift;
        private boolean banned = false;
        private String banDate;
        private boolean muted = false;
        private String muteDate;
        private String name;
        private String firstName;
        private String lastName;
        private String joinDate;
        private String tournamentBadgeStr;
        private FriendStatus friendStatus;
        private boolean goProfile;
        private int friendsCount;


        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setIsOnline(int isOnline) {
            this.isOnline = isOnline;
            return this;
        }

        public Builder setGamesWon(int gamesWon) {
            this.gamesWon = gamesWon;
            return this;
        }

        public Builder setGamesTotal(int gamesTotal) {
            this.gamesTotal = gamesTotal;
            return this;
        }

        public Builder setGamesPot(long gamesPot) {
            this.gamesPot = gamesPot;
            return this;
        }

        public Builder setPotMax(long potMax) {
            this.potMax = potMax;
            return this;
        }

        public Builder setGamesLost(int gamesLost) {
            this.gamesLost = gamesLost;
            return this;
        }

        public Builder setMoney(long money) {
            this.money = money;
            return this;
        }

        public Builder setVip(boolean vip) {
            this.vip = vip;
            return this;
        }

        public Builder setCurrentGift(String currentGift) {
            this.currentGift = currentGift;
            return this;
        }

        public Builder setBanned(boolean banned) {
            this.banned = banned;
            return this;
        }

        public Builder setBanDate(String banDate) {
            this.banDate = banDate;
            return this;
        }

        public Builder setMuted(boolean muted) {
            this.muted = muted;
            return this;
        }

        public Builder setMuteDate(String muteDate) {
            this.muteDate = muteDate;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setJoinDate(String joinDate) {
            this.joinDate = joinDate;
            return this;
        }

        public Builder setTournamentBadgeStr(String tournamentBadgeStr) {
            this.tournamentBadgeStr = tournamentBadgeStr;
            return this;
        }

        public Builder setFriendStatus(FriendStatus friendStatus) {
            this.friendStatus = friendStatus;
            return this;
        }

        public Builder setGoProfile(boolean goProfile) {
            this.goProfile = goProfile;
            return this;
        }

        public Builder setFriendsCount(int friendsCount) {
            this.friendsCount = friendsCount;
            return this;
        }

        public ProfileDetailsResponse build(){
            return new ProfileDetailsResponse(this);
        }
    }

}


