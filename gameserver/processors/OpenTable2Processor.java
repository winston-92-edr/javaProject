package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.EventDbLogType;
import com.mynet.gameserver.enums.TableType;
import com.mynet.gameserver.request.OpenTableRequest;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.OpenTable2Request;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenTable2Processor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(OpenTable2Processor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

        GameController controller = GameController.getInstance();

        if(!controller.isGeneric()){
            logger.error("Wrong server type");
            return;
        }

        if(controller.isInMaintenance()){
            return;
        }

        GameUser user = controller.getUser(message.getId());

        try {
            OpenTableRequest request = NetworkMessage.getGson().fromJson(message.getData(),OpenTableRequest.class);

            user.setEvent(EventDbLogType.CREATE_TABLE);

            int paired = request.isPaired() ? 1 : 0;
            controller.sendOpenTableWithAction(user, request.getRoomId(), paired);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}

