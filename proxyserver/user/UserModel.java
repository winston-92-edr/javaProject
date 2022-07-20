package com.mynet.proxyserver.user;

import com.mynet.shared.utils.Utils;
import com.mynet.socialserver.model.FriendRequestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.model.TournamentBadge;
import com.mynet.shared.resource.CacheController;

import java.beans.ConstructorProperties;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class UserModel {
    private static final Logger logger = LoggerFactory.getLogger(UserModel.class);

    public static final String F_ID = "fuid";
    public static final String F_PIGGY_BANK_LIMIT = "piggyLimit";
    public static final String F_STEPS_AWARD = "stepsAward";
    public static final String F_NAME = "name";

    public static final String F_GAMESERVERID = "gameserverID";
    public static final String F_PROXYID = "proxyID";
    public static final String F_GAMEID = "gameID";
    public static final String F_SOCIALID = "socialID";
    public static final String F_ROOMID = "roomID";
    public static final String F_TABLEID = "tableID";
    public static final String F_IP = "ip";
    public static final String F_PLATFORM = "platform";
    public static final String F_ANALYTICSSESSIONID = "analyticsSessionID";
    public static final String F_ANALYTICSDEVICEID = "analyticsDeviceID";
    public static final String F_APPVERSION = "appVersion";

    //db info
    public String fuid;
    public int gamesWon;
    public int gamesTotal;
    public long gamesPot;
    public long potMax;
    public int gamesLost;
    public long money;
    public boolean vip;
    public String currentGift;
    public long lastTimeGiftSent = 0;
    public int giftCount = 0;
    public boolean mobil;
    public boolean banned = false;
    public String banDate;
    public boolean muted = false;
    public String muteDate;
    public String name;
    public String firstName;
    public String lastName;
    public long joinDate;
    public boolean paid;
    public boolean guest;
    public String tournamentBadgeStr;
    public int experience;
    public int closedInvite;
    public int friendsCount;
    public  boolean goProfile;

    //TODO: USE THIS AFTER DEVELOPMENT FINISHED
    public boolean privateChat;

    // node mapping info
    public int roomID = -1;
    public int tableID = -1;
    public int proxyID = -1;
    public int gameID = -1;
    public int socialID = -1;
    public int gameServerId = -1;
    public long tickets;

    // analytics info
    public String analyticsDeviceId;
    public String analyticsSessionId;
    public String applicationVersion;

    public static UserModel createEmpty() {
        return new UserModel("");
    }

    public UserModel(String fuid) {
        this.fuid = fuid;
    }

    @ConstructorProperties({"FUID", "GAMES_WON", "GAMES_TOTAL", "GAMES_POT", "POT_MAX", "GAMES_LOST", "MONEY", "ISVIP", "CURRENT_GIFT", "IS_MOBILE", "BANNED", "MUTED", "NAME", "FIRSTNAME", "LASTNAME", "JOIN_DATE", "PAID_STATUS", "ticket","CLOSED_INVITE", "FRIENDS_COUNT", "GO_PROFILE", "PRIVATE_CHAT"})
    public UserModel(long fuid, int gamesWon, int gamesTotal, long gamesPot, long potMax, int gamesLost, long money, int vip, String currentGift, int mobil, Long banned, Long muted, String name, String firstName, String lastName,long joinDate, int paid, long tickets, int closedInvite, int friendsCount, int goProfile, int privateChat) {
        this.fuid = String.valueOf(fuid);
        this.gamesWon = gamesWon;
        this.gamesTotal = gamesTotal;
        this.gamesPot = gamesPot;
        this.potMax = potMax;
        this.gamesLost = gamesLost;
        this.money = money;
        this.vip = vip == 1;
        this.currentGift = currentGift;
        this.mobil = mobil == 1;
        this.name = Utils.getName(this.fuid, name);
        this.firstName = firstName;
        this.lastName = lastName;
        this.joinDate = joinDate;
        this.paid = paid == 1;
        this.guest = String.valueOf(fuid).startsWith("999000");
        this.tickets = tickets;
        if(banned != null){
            this.banned = System.currentTimeMillis() < banned;
            this.banDate = Utils.dateConversion(banned);
        }

        if(muted != null){
            this.muted = System.currentTimeMillis() < muted;
            this.muteDate = Utils.dateConversion(muted);
        }
        this.closedInvite = closedInvite;
        this.friendsCount = friendsCount;
        this.goProfile = goProfile == 0;
        this.privateChat = privateChat == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserModel userModel = (UserModel) o;

        return fuid.equals(userModel.fuid);
    }

    public String getTournamentBadge() {
        if (tournamentBadgeStr == null || tournamentBadgeStr.length() == 0) {
            TournamentBadge badgeObj = CacheController.getInstance().getUserTournamentBadge(fuid);
            tournamentBadgeStr = badgeObj != null ? badgeObj.toString() : "0";
        }
        return tournamentBadgeStr;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "fuid='" + fuid + '\'' +
                ", gamesWon=" + gamesWon +
                ", gamesTotal=" + gamesTotal +
                ", gamesPot=" + gamesPot +
                ", potMax=" + potMax +
                ", gamesLost=" + gamesLost +
                ", money=" + money +
                ", vip=" + vip +
                ", currentGift='" + currentGift + '\'' +
                ", mobil=" + mobil +
                ", banned=" + banned +
                ", banDate='" + banDate + '\'' +
                ", muted=" + muted +
                ", muteDate='" + muteDate + '\'' +
                ", name='" + name + '\'' +
                ", joinDate=" + joinDate +
                ", paid=" + paid +
                ", roomID=" + roomID +
                ", tableID=" + tableID +
                ", proxyID=" + proxyID +
                ", gameID=" + gameID +
                ", socialID=" + socialID +
                '}';
    }
}
