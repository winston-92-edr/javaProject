package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.actions.SitTableAction;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.enums.PlayerSide;
import com.mynet.shared.logs.TournamentEventLog;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.SitTableRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReadyForGameProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(ReadyForGameProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameController gameController = GameController.getInstance();
            SitTableRequest sitTableRequest = NetworkMessage.CreateMessage(message.getData(), SitTableRequest.class);
            Table table = gameController.getTable(sitTableRequest.getTableId());
            if(table == null) return;

            ConcurrentHashMap<String, Integer> readyUsers = table.addReadyUser(message.getId(), sitTableRequest.getSide());

            int roomId = GameController.TOURNAMENT_ROOM_ID;
            if(table.isTableReady()){

                for (Map.Entry<String, Integer> entry : readyUsers.entrySet()){
                    String userId = entry.getKey();
                    int side = entry.getValue();

                    GameUser user = gameController.getUser(userId);
                    NetworkMessage resp = new NetworkMessage(GameCommands.SIT_TABLE_REQUEST);

                    if (gameController.isInMaintenance()) {
                        gameController.getNodeToProxy().sendError(user, ErrorCode.MAINTENANCE_MODE);
                        continue;
                    }

                    user.setRoomId(roomId);

                    user.beginTournamentEvent(TournamentEventLog.Type.SIT_TABLE);
                    user.fillTournamentEvent(user.getTournamentId(), true, sitTableRequest.getTableId(), 0, 0);
                    user.endTournamentEvent();

                    table.addTableAction(new SitTableAction(table, user, sitTableRequest.getTableId(), PlayerSide.forCode(side), false, "TOURNAMENT_MATCHMAKING"));
                }
            }else{
                GameUser user = GameController.getInstance().getUser(message.getId());

                if(user != null) {
                    gameController.sendNetworkMessage(user, GameCommands.CANCEL_MATCHMAKING, "");
                }
            }

        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }
    }
}
