package com.mynet.socialserver.processors;

import com.google.gson.Gson;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;
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

        SocialController controller = SocialController.getInstance();
        ProxyUser user = controller.getUser(message.getId());
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
