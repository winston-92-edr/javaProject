package com.mynet.proxyserver.network;

import com.mynet.proxyserver.processors.*;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.MessageProcessController;

public class ProxyServerMessageController {
    MessageProcessController controller;

    public ProxyServerMessageController() {
        MessageProcessController.init();

        this.controller = MessageProcessController.getInstance();
        this.controller.registerCommand(GameCommands.MATCH, new ProxyMatchProcessor());
        this.controller.registerCommand(GameCommands.QUICK_PLAY_SERVER, new ProxyQuickPlayProcessor());
        this.controller.registerCommand(GameCommands.CHANGE_GAME_NODE, new ChangeGameNodeProcessor());
        this.controller.registerCommand(GameCommands.SIT_TABLE_SERVER, new ProxySitTableProcessor());
        this.controller.registerCommand(GameCommands.JOIN_AN_AUDIENCE_SERVER, new ProxyJoinAudienceProcessor());
        this.controller.registerCommand(GameCommands.PROXY_ENTER_TABLE, new ProxyEnterTableProcessor());
        this.controller.registerCommand(GameCommands.CHECK_AVAILABLE_INVITE, new CheckAvailableInviteProcessor());
        this.controller.registerCommand(GameCommands.CHANGE_USER_SERVER_TYPE, new ChangeUserServerTypeProcessor());
        this.controller.registerCommand(GameCommands.SIT_TABLE_REQUEST, new ProxySitTableRequestProcessor());
        this.controller.registerCommand(GameCommands.CHAT_LEFT_TABLE, new ChatLeftTableProcessor());
        this.controller.registerCommand(GameCommands.CHAT_ENTER_TABLE, new ChatEnterTableProcessor());
        this.controller.registerCommand(GameCommands.PING, new PingProcessor());
    }
}
