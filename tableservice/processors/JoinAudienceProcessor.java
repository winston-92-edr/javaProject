package com.mynet.tableservice.processors;

import com.google.gson.Gson;
import com.mynet.proxyserver.network.StringUtil;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.SitTableRequest;
import com.mynet.tableservice.service.ServiceProxyDataWrapper;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinAudienceProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(JoinAudienceProcessor.class);
    private Gson gson;

    public JoinAudienceProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {

            TableService service = TableService.getInstance();
            ServiceProxyDataWrapper wrapper = gson.fromJson(message.getData(), ServiceProxyDataWrapper.class);
            int proxyId = wrapper.getProxyId();
            int tableId = Integer.parseInt(wrapper.getData());

            int gameNodeId = service.getTableGameNode(tableId);
            if(gameNodeId == -1){
                logger.warn("Game node NOT FOUND for: " + tableId);
                return;
            }

            SitTableRequest request = new SitTableRequest();
            request.setTableId(tableId);
            request.setGameNodeId(gameNodeId);

            String json = gson.toJson(request);

            NetworkMessage response = new NetworkMessage(GameCommands.JOIN_AN_AUDIENCE_SERVER);
            response.setData(json);
            service.sendNetworkMessage(message.getId(), proxyId, response);
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

    }
}
