package com.mynet.tableservice.processors;

import com.google.gson.Gson;
import com.mynet.gameserver.model.AvailableTableModel;
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

public class SitTableProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(SitTableProcessor.class);
    private Gson gson;

    public SitTableProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {

            TableService service = TableService.getInstance();
            ServiceProxyDataWrapper wrapper = gson.fromJson(message.getData(), ServiceProxyDataWrapper.class);
            int proxyId = wrapper.getProxyId();

            String[] arr = StringUtil.processRawString(wrapper.getData(), ";");
            int side = Integer.parseInt(arr[0]);
            int tableId = Integer.parseInt(arr[1]);

            int gameNodeId = service.getTableGameNode(tableId);
            if(gameNodeId == -1){
                logger.warn("Game node NOT FOUND for: " + tableId);
                return;
            }

            SitTableRequest request = new SitTableRequest();
            request.setTableId(tableId);
            request.setSide(side);
            request.setGameNodeId(gameNodeId);

            String json = gson.toJson(request);

            NetworkMessage response = new NetworkMessage(GameCommands.SIT_TABLE_SERVER);
            response.setData(json);
            service.sendNetworkMessage(message.getId(), proxyId, response);
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

    }
}
