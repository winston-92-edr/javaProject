package com.mynet.socialserver.processors;

import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.model.TournamentBadge;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.utils.DateUtils;
import com.mynet.shared.utils.Utils;
import com.mynet.socialserver.SocialController;
import com.mynet.socialserver.enums.FriendStatus;
import com.mynet.socialserver.request.ProfileDetailsRequest;
import com.mynet.socialserver.response.ProfileDetailsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ProfileDetailsProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(ProfileDetailsProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        SocialController socialController = SocialController.getInstance();
        ProxyUser user = socialController.getUser(message.getId());

        try {
            ProfileDetailsRequest request = NetworkMessage.getGson().fromJson(message.getData(),ProfileDetailsRequest.class);
            String userId = request.getUserId();

            ProxyUser requestedUser = SocialController.getInstance().getUser(userId);
            DBController db = DBController.getInstance();
            UserModel gameUserModel = db.getUser(userId);
            int isOnline = requestedUser == null ? 0 : 1;

            Date joinDate = new Date(gameUserModel.joinDate);
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            String joinDateStr = formatter.format(joinDate);
            joinDateStr = DateUtils.changeDateToTurkish(joinDateStr);

            FriendStatus friendStatus = db.isFriend(message.getId(), userId) ? FriendStatus.NOT_FACEBOOK_FRIEND : FriendStatus.NOT_FRIEND;

            int friendsCount = 0;

            if(requestedUser != null){
                friendsCount = requestedUser.getFriendsCount();
            }else{
                friendsCount = gameUserModel.friendsCount;
            }

            ProfileDetailsResponse.Builder builder = new ProfileDetailsResponse.Builder();

            String profileId = gameUserModel.fuid;

            String firstName = Utils.getName(profileId, gameUserModel.firstName);

            TournamentBadge badgeObj= CacheController.getInstance().getUserTournamentBadge(userId);
            String tournamentBadgeStr = badgeObj != null ? badgeObj.toString() : "0";

            ProfileDetailsResponse response = builder
                    .setId(profileId)
                    .setIsOnline(isOnline)
                    .setFirstName(firstName)
                    .setLastName(gameUserModel.lastName)
                    .setJoinDate(joinDateStr)
                    //.setFriendRequests(gameUserModel.friendRequests)
                    .setGamesTotal(gameUserModel.gamesTotal)
                    .setGamesLost(gameUserModel.gamesLost)
                    .setGamesWon(gameUserModel.gamesWon)
                    .setGamesPot(gameUserModel.gamesPot)
                    .setPotMax(gameUserModel.potMax)
                    .setCurrentGift(gameUserModel.currentGift)
                    .setMoney(gameUserModel.money)
                    .setVip(gameUserModel.vip)
                    .setTournamentBadgeStr(tournamentBadgeStr)
                    .setBanned(gameUserModel.banned)
                    .setBanDate(gameUserModel.banDate)
                    .setMuted(gameUserModel.muted)
                    .setMuteDate(gameUserModel.muteDate)
                    .setFriendStatus(friendStatus)
                    .setFriendsCount(friendsCount)
                    .setGoProfile(gameUserModel.goProfile)
                    .build();

            NetworkMessage networkMessage = new NetworkMessage(GameCommands.GET_PROFILE_DETAILS);
            networkMessage.setData(NetworkMessage.getGson().toJson(response));
            SocialController.getInstance().getNodeToProxy().addServerMessage(networkMessage, user);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
