package com.mynet.tableservice.processors;

import com.google.gson.Gson;
import com.mynet.gameserver.model.AvailableTableModel;
import com.mynet.gameserver.model.TableFilterModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.tableservice.service.ServiceProxyDataWrapper;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickPlayProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(QuickPlayProcessor.class);
    private Gson gson;

    public QuickPlayProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {

            ServiceProxyDataWrapper wrapper = gson.fromJson(message.getData(), ServiceProxyDataWrapper.class);
            String userId = message.getId();
            int proxyId = wrapper.getProxyId();

            AvailableTableModel quickTable = TableService.getInstance().getQuickTable(userId, new TableFilterModel());
            NetworkMessage response = new NetworkMessage(GameCommands.QUICK_PLAY_SERVER);


            if(quickTable == null)response.setData("-1");
            else {
                String json = gson.toJson(quickTable);
                response.setData(json);
            }

            TableService.getInstance().sendNetworkMessage(message.getId(), proxyId, response);
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

    }
}
