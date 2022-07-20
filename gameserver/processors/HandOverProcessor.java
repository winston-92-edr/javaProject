package com.mynet.gameserver.processors;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.okey.CardHandler;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.actions.HandOverAction;
import com.mynet.gameserver.request.HandOverRequest;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.launchers.GameServerLauncher;
import com.mynet.shared.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class HandOverProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(HandOverProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());
        if(user == null) return;

        try {
            HandOverRequest request = NetworkMessage.getGson().fromJson(message.getData(), HandOverRequest.class);

            //TODO:????
            if (request.getHand() == null) {
                Table table = controller.getTable(user.getTableId());
                table.sendErrorMessage(user, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.FINISHING_HAND_NULL)));

                CardHandler chandler = table.getCardHandler();
                String hand = chandler.getSideHand(table.getGameTurn());

                int nodeId = GameServerLauncher.currentNode.getId();
                logger.debug("gameLobby.getLobbyId() : " + nodeId + "; " + " table: " + table.getTableId());
                HandOverAction.handOverLog(
                        user.getfuid(),
                        table.getGameId() + "",
                        hand + "-",
                        "-",
                        "finishHandNull",
                        "-",
                        user.getPlatform(),
                        nodeId,
                        table.getTableId(),
                        table
                );
                return;
            }

            if(user.getTableId() != -1) {
                Table table = controller.getTable(user.getTableId());
                HandOverAction handOverAction = new HandOverAction(table, user, String.valueOf(user.getTableId()), request.getSide(), request.getcId(), message.getId(), request.isGoDouble(), request.getHand());
                table.addGameAction(handOverAction);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
