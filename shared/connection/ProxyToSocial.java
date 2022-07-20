package com.mynet.shared.connection;

import com.mynet.shared.network.*;
import com.mynet.shared.types.ServerType;
import com.mynet.socialserver.processors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.socialserver.SocialController;

public class ProxyToSocial implements ServerToServerMessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ProxyToSocial.class);
    private MessageProcessController messageController;

    public ProxyToSocial(ServerType serverType) {
        messageController = MessageProcessController.getInstance();

        registerSharedMessageProcessors();

        switch (serverType){
            case TOURNAMENT:
                registerTournamentMessageProcessors();
                break;
            case GENERIC:
                registerGenericMessageProcessors();
                break;
        }
    }

    public void registerSharedMessageProcessors(){
        messageController.registerCommand(GameCommands.ADD_USER, new AddUserProcessor());
        messageController.registerCommand(GameCommands.REMOVE_USER, new RemoveUserProcessor());
        messageController.registerCommand(GameCommands.SERVER_USER_STATUS_UPDATED, new UserStatusUpdateProcessor());
        messageController.registerCommand(GameCommands.SOCKET_DISCONNECT, new SocketDisconnectProcessor());

        //TODO: Open after client development
        //messageController.registerCommand(GameCommands.UPDATE_USER_OPTIONS, new UpdateSettingsProcessor());
    }

    public void registerTournamentMessageProcessors(){
        messageController.registerCommand(GameCommands.GET_TOURNAMENTS_INFO, new TournamentInfoProcessor());
        messageController.registerCommand(GameCommands.JOIN_TOURNAMENT, new JoinTournamentProcessor());
        messageController.registerCommand(GameCommands.CLAIM_AWARD, new ClaimAwardProcessor());
        messageController.registerCommand(GameCommands.CHECK_USER_TOURNAMENT, new CheckUserTournamentProcessor());
        messageController.registerCommand(GameCommands.GET_TOURNAMENT_PROFILE_DETAILS, new TournamentProfileDetailProcessor());
    }

    public void registerGenericMessageProcessors(){
        messageController.registerCommand(GameCommands.GET_PROFILE_DETAILS, new ProfileDetailsProcessor());
        messageController.registerCommand(GameCommands.ADD_FRIEND, new AddFriendProcessor());
        messageController.registerCommand(GameCommands.FRIEND_REQUEST, new FriendRequestProcessor());
        // messageController.registerCommand(GameCommands.ACTIVE_FRIEND, new SetActiveFriendProcessor());
        messageController.registerCommand(GameCommands.REMOVE_FRIEND, new RemoveFriendProcessor());
        messageController.registerCommand(GameCommands.GO_TO_YOUR_FRIEND, new GoToYourFriendProcessor());
        messageController.registerCommand(GameCommands.GO_TO_FRIEND, new GoToFriendProcessor());
        messageController.registerCommand(GameCommands.PRIVATE_CHAT, new PrivateChatProcessor());
        messageController.registerCommand(GameCommands.START_PRIVATE_CHAT, new StartPrivateChatProcessor());
        messageController.registerCommand(GameCommands.GET_ONLINE_FRIENDS, new GetOnlineFriendsProcessor());
        messageController.registerCommand(GameCommands.PURCHASE_NOTIFICATION, new PurchaseNotificationProcessor());
        messageController.registerCommand(GameCommands.PURCHASE_NOTIFICATION_RESULT, new PurchaseNotificationResultProcessor());
    }

    public void processMessage(NetworkMessage request) throws InvalidServerMessage {
        if (request.getCmd() != GameCommands.REMOVE_USER && request.getCmd() != GameCommands.ADD_USER) {
            if (request.getId() != null) {
                SocialController.getInstance().resetUser(request.getId());
            }
        }

        messageController.processMessage(request);
    }
}
