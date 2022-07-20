package com.mynet.shared.connection;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.processors.*;
import com.mynet.shared.network.*;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyToGame implements ServerToServerMessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(ProxyToGame.class);

    public ProxyToGame(ServerType serverType) {
        registerSharedMessageProcessors();

        switch (serverType){
            case GENERIC:
                registerGenericMessageProcessors();
                break;
        }
    }

    private void registerGenericMessageProcessors(){
        MessageProcessController.getInstance().registerCommand(GameCommands.CHECK_AVAILABLE_INVITE, new CheckAvailableInviteProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SEND_INVITE_REQUEST, new SendInviteProcessor());
    }

    private void registerSharedMessageProcessors() {
        MessageProcessController.getInstance().registerCommand(GameCommands.ADD_USER, new AddUserProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.REMOVE_USER, new RemoveUserProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.MATCH, new MatchProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SIT_TABLE_REQUEST, new ReadyForGameProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.LEFT_TABLE, new LeftTable2Processor());
        MessageProcessController.getInstance().registerCommand(GameCommands.GET_GAME_STATUS, new GameStatusProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SEND_USER_ACTION, new UserActionProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.GET_CARD_FROM_DECK, new GetCardFromDeckProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SEND_HAND_OVER, new HandOverProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SEND_GO_DOUBLE, new GoDoubleProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SIT_TABLE, new SitTableProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SIT_TABLE_2, new SitTable2Processor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SIT_NODE_TABLE, new SitTable2Processor());
        MessageProcessController.getInstance().registerCommand(GameCommands.QUICK_PLAY, new QuickPlayProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.QUICK_PLAY_2, new QuickPlay2Processor());
        MessageProcessController.getInstance().registerCommand(GameCommands.ENTER_ROOM, new EnterRoomProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.JOIN_AN_AUDIENCE, new JoinAudienceProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.JOIN_NODE_TABLE, new JoinAudienceProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.GET_THROWN_CARDS, new GetThrownCardsProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.OPEN_TABLE, new OpenTable2Processor());
        MessageProcessController.getInstance().registerCommand(GameCommands.GET_USERS_IN_LOBBY, new GetUsersInLobbyProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SEND_GIFT,new SendGiftProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.LEAVE_ROOM,new LeaveRoomProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.ENTER_ROOM_2, new EnterRoom2Processor());
        MessageProcessController.getInstance().registerCommand(GameCommands.INVITE_RESULT, new AcceptInviteProcess());
        MessageProcessController.getInstance().registerCommand(GameCommands.SERVER_USER_STATUS_UPDATED, new UserStatusUpdateProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.TABLE_CHAT, new SendChatMessageProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.MUTE_USER, new MuteUserProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.GAME_ENTER_TABLE, new GameEnterTableProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SEND_USER_STATE, new SendStateProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SUSPEND, new ClientSuspendProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.AWAKE, new ClientAwakeProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SUSPEND_AWAKE, new ClientSuspendOrAwakeProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.GET_USER_GIFTS, new GetUserGiftsProcessor());
    }

    @Override
    public void processMessage(NetworkMessage request) throws InvalidServerMessage {
        if (request.getCmd() != GameCommands.REMOVE_USER && request.getCmd() != GameCommands.ADD_USER) {
            if (request.getId() != null) {
                GameController.getInstance().fixUser(request.getId());
            }
        }

        MessageProcessController.getInstance().processMessage(request);
    }
}
