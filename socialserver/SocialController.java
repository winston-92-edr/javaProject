package com.mynet.socialserver;

import com.mynet.gameserver.model.RemoveGameUserModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.utils.Utils;
import com.mynet.socialserver.response.UpdateFriendStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.connection.NodeToProxy;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.user.ProxyUser;

import javax.naming.CannotProceedException;
import java.util.concurrent.ConcurrentHashMap;

public class SocialController {
    private static final Logger logger = LoggerFactory.getLogger(SocialController.class);
    private ConcurrentHashMap<String, ProxyUser> users;
    private NodeToProxy nodeToProxy;
    private CacheController cacheController;

    private ServerGlobalVariables serverGlobalVariables;
    private int nodeId;

    private static SocialController INSTANCE;

    public int getNodeId() {
        return nodeId;
    }

    public NodeToProxy getNodeToProxy() {
        return nodeToProxy;
    }

    public SocialController(int nodeId) {
        this.nodeId = nodeId;
        users = new ConcurrentHashMap<>();
        nodeToProxy = new NodeToProxy();
        cacheController = CacheController.getInstance();
        serverGlobalVariables = ServerGlobalVariables.getInstance();

        addRedisEventListeners();
    }


    public static void init(int nodeId) {
        if(INSTANCE == null){
            INSTANCE = new SocialController(nodeId);
        }
    }

    public static SocialController getInstance(){
        return INSTANCE;
    }

    public ProxyUser getUser(String id) {
        return users.get(id);
    }

    private void addRedisEventListeners() {
        logger.info("Social started to listen Redis Events");

        cacheController.listenMuteUpdateEvents((charSequence, userId) -> {
            try {
                ProxyUser user = getUser((String) userId);
                if(user == null) return;

                Long muted = DBController.getInstance().getUserMuted(user.getId());

                if(muted != null){
                    user.getUserModel().muted = System.currentTimeMillis() < muted;
                    user.getUserModel().muteDate = Utils.dateConversion(muted);
                }
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        });

        cacheController.listenVipUpdateEvents((charSequence, userId) -> {
           try {
               ProxyUser user = getUser((String) userId);
               if(user == null) return;

               user.updateVip(true);
           }catch (Exception e){
               logger.error(e.getMessage(), e);
           }
        });
        cacheController.listenTicketUpdateEvents((charSequence, userId) -> {
            try {
                ProxyUser user = getUser((String) userId);
                if(user == null) return;

                int tickets = DBController.getInstance().getUserTickets((String) userId);
                user.updateTicket(tickets);
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        });
        cacheController.listenMoneyUpdateEvents((charSequence, userId) -> {
            try {
                ProxyUser user = getUser((String) userId);
                if(user == null) return;

                long money = DBController.getInstance().getUserMoney((String) userId);
                user.updateMoney(money);
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        });
        cacheController.listenRemoveSocialUser((charSequence, removeGameUserModel) -> {
            if(removeGameUserModel.getGameNodeId() == getNodeId()) return;

            removeUser(removeGameUserModel.getUserId());
        });
        cacheController.listenUpdateSessionId((charSequence, info) -> {

            String[] data = info.split(":");

            if (data.length < 2) return;

            String id = data[0];
            String sessionId = data[1];

            ProxyUser user = getUser(id);
            if (user == null) return;

            user.setAnalyticsSessionId(sessionId);
        });
    }

    boolean hasUser(String id) {
        return users.containsKey(id);
    }

    boolean hasUser(Object id) {
        String userId = (String) id;
        return users.containsKey(userId);
    }


    public void resetUser(String userID) {
        if (hasUser(userID)) {
            ProxyUser u = getUser(userID);
            u.setLastActiveTime(System.currentTimeMillis());
        }else {
            try {
                createUser(userID);
                CacheController.getInstance().publishRemoveSocialUser(new RemoveGameUserModel(getNodeId(), userID));
            }catch (CannotProceedException e){
                logger.error(e.getMessage(), e);
            }
        }
    }

    public ProxyUser createUser(String userID) throws CannotProceedException {

        if (hasUser(userID)) {
            users.remove(userID);
        }

        ProxyUser user = null;

        UserModel userModel = DBController.getInstance().getUser(userID);
        if (userModel != null) {
            user = new ProxyUser(userModel);
            //HashMap<String, FriendRequestModel> friendRequests = DBController.getInstance().getFriendRequests(userID);
            //user.setFriendRequests(friendRequests);

            UserModel model = cacheController.getUserGameModel(user.getId());
            user.setProxyId(model.proxyID);
            user.setChatList(cacheController.getChatList(user.getId()));
            user.setAnalyticsSessionId(userModel.analyticsSessionId);
            user.setAnalyticsDeviceId(userModel.analyticsDeviceId);
            user.setApplicationVersion(userModel.applicationVersion);
            user.setConnected(true);
            addUser(user);
        }

        return user;
    }

    public ProxyUser createUser(String userID, int friendsCount) throws CannotProceedException {

        if (hasUser(userID)) {
            users.remove(userID);
        }

        ProxyUser user = null;

        UserModel userModel = DBController.getInstance().getUser(userID);
        if (userModel != null) {
            user = new ProxyUser(userModel);
            //HashMap<String, FriendRequestModel> friendRequests = DBController.getInstance().getFriendRequests(userID);
            //user.setFriendRequests(friendRequests);

            UserModel model = cacheController.getUserGameModel(user.getId());
            user.setProxyId(model.proxyID);
            user.setChatList(cacheController.getChatList(user.getId()));
            user.setAnalyticsSessionId(userModel.analyticsSessionId);
            user.setAnalyticsDeviceId(userModel.analyticsDeviceId);
            user.setApplicationVersion(userModel.applicationVersion);
            user.setFriendsCount(friendsCount);
            user.setConnected(true);
            addUser(user);
        }

        return user;
    }

    public boolean addUser(ProxyUser user) {
        String userID = user.getId();

        if (users.containsKey(userID)) {
            return false;
        }

        users.put(userID, user);

        cacheController.setSocialNode(userID, nodeId);

        return true;
    }

    public boolean removeUser(String id) {
        if (hasUser(id)) {
            clearUserFriendList(id);
            users.remove(id);
            cacheController.setSocialNode(id, -1);
            cacheController.deleteChatList(id);
            //DBController.getInstance().removeAllFriendRequests(id);
            return true;
        }
        return false;
    }

    private void clearUserFriendList(String id) {
        ProxyUser user = getUser(id);
        notifyFriendStatus(user, -1,false);
        //cacheController.deleteOnlineFriends(id);
    }

    public ConcurrentHashMap<String, ProxyUser> getUsers() {
        return users;
    }

    public void notifyFriendStatus(ProxyUser user, int userProxyId, boolean online) {

        if(!online || !user.isOnlineFriendsSet()) {
            for (String id : user.getOnlineFriends().keySet()) {

                String userId = user.getId();

                //SAME SOCIAL NODE
                ProxyUser friend = this.getUser(id);
                if (friend != null) {

                    if (online) {
                        if (!friend.hasOnlineFriend(userId)) {

                            //NOTIFY FRIEND
//                            NetworkMessage response = new NetworkMessage(GameCommands.UPDATE_FRIEND_STATUS);
//                            response.setDataAsJSON(new UpdateFriendStatusResponse(userId, online));
//                            this.getNodeToProxy().addServerMessage(response, friend);
                        }

                        friend.addOnlineFriend(userId, userProxyId);
                    } else {

                        //NOTIFY FRIEND
//                        NetworkMessage response = new NetworkMessage(GameCommands.UPDATE_FRIEND_STATUS);
//                        response.setDataAsJSON(new UpdateFriendStatusResponse(userId, online));
//                        this.getNodeToProxy().addServerMessage(response, friend);

                        friend.removeOnlineFriend(userId);
                    }
                } else {
                    //TODO: If there will be multiple social nodes send this message to friends proxy to transfer its social node
                }
            }

            if (online) {
                user.setOnlineFriendsSet();
            }
        }
    }
}
