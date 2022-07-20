package com.mynet.gameserver.processors;

import com.google.gson.Gson;
import com.mynet.gameserver.GameController;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserStatusUpdateProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(UserStatusUpdateProcessor.class);

    private Gson gson;

    public UserStatusUpdateProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());
        if(user == null) return;

        try {
            BasicUserModel userModel = gson.fromJson(message.getData(), BasicUserModel.class);
            user.updateMoney(userModel.getMoney());
            user.updateTicket(userModel.getTicket());
            user.updateVip(userModel.isVip());

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
