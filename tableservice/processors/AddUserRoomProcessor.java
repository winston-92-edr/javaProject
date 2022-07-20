package com.mynet.tableservice.processors;

import com.google.gson.Gson;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.tableservice.actions.AddUserRoomAction;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddUserRoomProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(AddUserRoomProcessor.class);
    private Gson gson;

    public AddUserRoomProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            BasicUserModel user = gson.fromJson(message.getData(), BasicUserModel.class);
            TableService.getInstance().updateUserRoom(user, true);
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }
    }
}
