package com.mynet.gameserver.actions;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;

public class JoinAnAudienceAction extends TableAction {
    public JoinAnAudienceAction(Table table, GameUser user) {
        super(table, user);
    }

    @Override
    public boolean process() {
//    GameController.getInstance().joinAnAudience(user, table.getTableId() + "");
      return true;
    }

    @Override
    public GameAction getGameAction() {
        return null;
    }

    @Override
    public String getName() {
        return "Join Audience";
    }

}
