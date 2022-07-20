package com.mynet.tableservice.processors;

import com.google.gson.Gson;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.tableservice.actions.RemoveUserAction;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveUserProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(RemoveUserProcessor.class);
    private Gson gson;

    public RemoveUserProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            TableService.getInstance().removeUser(message.getId());
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

    }
}
