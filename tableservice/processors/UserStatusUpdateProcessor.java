package com.mynet.tableservice.processors;

import com.google.gson.Gson;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.tableservice.service.ServiceUser;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserStatusUpdateProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(com.mynet.gameserver.processors.UserStatusUpdateProcessor.class);

    private Gson gson;

    public UserStatusUpdateProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

        TableService controller = TableService.getInstance();
        ServiceUser user = controller.getUser(message.getId());
        if(user == null) return;

        try {
            BasicUserModel userModel = gson.fromJson(message.getData(), BasicUserModel.class);
            user.getUser().setTicket(userModel.getTicket());
            user.getUser().setMoney(userModel.getMoney());
            user.getUser().setVip(userModel.isVip());

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
