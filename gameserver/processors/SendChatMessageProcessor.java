package com.mynet.gameserver.processors;

import com.google.gson.Gson;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.request.TableChatRequest;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendChatMessageProcessor implements MessageProcessor {
    private Gson gson;

    public SendChatMessageProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        GameController gameController = GameController.getInstance();
        TableChatRequest request = NetworkMessage.CreateMessage(message.getData(), TableChatRequest.class);
        gameController.sendTableChatMessage(message.getId(), request.getMessage());
    }
}
