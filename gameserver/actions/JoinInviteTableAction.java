package com.mynet.gameserver.actions;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinInviteTableAction extends TableAction {

    private static Logger logger = LoggerFactory.getLogger(JoinInviteTableAction.class);
    private final int roomId;

    public JoinInviteTableAction(Table table, GameUser user, int roomId) {
        super(table, user);
        this.roomId = roomId;
    }

    @Override
    public boolean process() {
        try {
            GameController gameLobby = GameController.getInstance();
            gameLobby.sendNetworkMessage(user, GameCommands.INVITE_RESULT, Quick_Start_Game.JoinInviteTable(user, roomId, table));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return true;
    }

    @Override
    public GameAction getGameAction() {
        return null;
    }

    @Override
    public String getName() {
        return "Join Invite Table";
    }
}

