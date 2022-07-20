package com.mynet.gameserver.processors;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.enums.TableType;
import com.mynet.gameserver.GameController;
import com.mynet.matchserver.GameUser;
import com.mynet.matchserver.response.MatchingResponse;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.types.GamePlayStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(MatchProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        MatchingResponse request = NetworkMessage.CreateMessage(message.getData(), MatchingResponse.class);

        GameController controller = GameController.getInstance();
        int tableId = request.getTableId();

        BasicUserModel[] users = request.getUsers();
        int roomId = GameController.TOURNAMENT_ROOM_ID;
        GamePlayStatusType createStatus = controller.createTable(null, tableId, roomId, 0, request.getSideCount(), TableType.PRIVATE, true);
        try {
            for (BasicUserModel user: users){
                GameUser gameUser = controller.getUser(user.getId());

                NetworkMessage resp = new NetworkMessage(GameCommands.SIT_TABLE_REQUEST);

                if (controller.isInMaintenance()) {
                    controller.getNodeToProxy().sendError(gameUser, ErrorCode.MAINTENANCE_MODE);
                    continue;
                }

                if(!createStatus.equals(GamePlayStatusType.VALID)){
                    controller.getNodeToProxy().sendError(gameUser, ErrorCode.GENERAL_ERROR);
                    logger.error("MATCH FAILED! | createStatus is NOT valid");
                    continue;
                }

                gameUser.setTournamentId(user.getTournamentId());
            }
        }catch (Exception ex){

        }
    }

}
