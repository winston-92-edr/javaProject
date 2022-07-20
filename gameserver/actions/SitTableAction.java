package com.mynet.gameserver.actions;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.enums.InfoCode;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.response.TableInfoResponse;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.builders.ErrorResponseBuilder;
import com.mynet.shared.enums.PlayerSide;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.response.InfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SitTableAction extends TableAction {
    private static final Logger logger = LoggerFactory.getLogger(SitTableAction.class);

    private int tableId;
    private PlayerSide side;
    private boolean isFromQuickPlay;
    private String from;


    public SitTableAction(Table table, GameUser user, int tableId, PlayerSide side, boolean isFromQuickPlay, String from) {
        super(table, user);
        this.tableId = tableId;
        this.side = side;
        this.isFromQuickPlay = isFromQuickPlay;
        this.from = from;
    }


    @Override
    public boolean process() {
        try {

            GameController gameController = GameController.getInstance();

            if (gameController.isGeneric()) {

                Table table = gameController.getTable(tableId);
                if(table == null) {
                    return false;
                }

                int requiredMoney = table.getMinBet();

                if (table.checkGamersIp(user)) {
                    gameController.sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.SAME_IP)));
                } else {

                    boolean canAfford = user.canAfford(-requiredMoney);
                    boolean isLowBet = gameController.getRoom(table.getRoomId()).isLowBet(user.getMoney());

                    if (table.canEnterToVipTable(user) && ((canAfford && !isLowBet) || table.availableToBotSit(side.getValue(), user))) {
                        table.sitTable(side, user, from);
                    } else if (!table.canEnterToVipTable(user) && canAfford && !isLowBet) {
                        ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.NOT_VIP_TABLE).createErrorResponse();
                        gameController.sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(error));
                    } else if(isLowBet) {
                        ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.LOW_BET_TABLE).createErrorResponse();
                        gameController.sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(error));
                    } else {
                        ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.NOT_ENOUGH_MONEY_TABLE).createErrorResponse();
                        gameController.sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(error));
                    }

                }

            } else {
                table.sitTable(side, user, from);
            }
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
        return "Sit Table";
    }
}
