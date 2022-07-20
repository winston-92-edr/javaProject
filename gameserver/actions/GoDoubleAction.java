package com.mynet.gameserver.actions;

import com.mynet.gameserver.okey.GameSide;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.response.GoDoubleResponse;
import com.mynet.matchserver.GameUser;
import com.mynet.questservice.quests.category.GameActionCategoryInfo;
import com.mynet.questservice.quests.category.QuestCategory;
import com.mynet.questservice.quests.category.QuestCategoryInfo;
import com.mynet.questservice.quests.types.GameActionType;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.NetworkMessage;

public class GoDoubleAction extends TableAction {
    public GoDoubleAction(Table table, GameUser user) {
        super(table, user);
    }

    @Override
    public boolean process() {
        try {
            if (table != null) {
                int side = table.getGamerSide(user.getfuid());
                if (side != -1) {
                    GameSide gside = table.getGameSide(side);
                    if (gside != null) {

                        if(gside.getGoDouble() != 1) {
                            QuestCategoryInfo questCategoryInfo = new GameActionCategoryInfo(user.getfuid(), GameActionType.GO_DOUBLE.getValue());
                            RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.GAME_ACTION, questCategoryInfo);
                        }

                        gside.setGoDouble(1);

                        GoDoubleResponse.Builder builder = new GoDoubleResponse.Builder();
                        GoDoubleResponse goDoubleResponse = builder.setSide(side)
                                .setUserId(user.getfuid())
                                .build();

                        table.sendGoDouble(NetworkMessage.getGson().toJson(goDoubleResponse));
                    }
                }
            }
        } catch (Exception ex) {

        }
        return true;
    }

    @Override
    public GameAction getGameAction() {
        return null;
    }

    @Override
    public String getName() {
        return "Go Double";
    }
}
