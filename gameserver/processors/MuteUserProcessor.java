package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.utils.Utils;

public class MuteUserProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        GameController gameController = GameController.getInstance();
        DBController dbController = DBController.getInstance();

        GameUser user = gameController.getUser(message.getId());
        Long muted = dbController.getUserMuted(user.getfuid());

        if(muted != null){
            user.getUserModel().muted = System.currentTimeMillis() < muted;
            user.getUserModel().muteDate = Utils.dateConversion(muted);
        }

    }
}
