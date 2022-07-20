package com.mynet.tableservice.processors;

import com.google.gson.Gson;
import com.mynet.gameserver.model.TableInfoModel;
import com.mynet.tableservice.enums.TableFullnessFilter;
import com.mynet.tableservice.enums.TablePairedFilter;
import com.mynet.tableservice.enums.TableRobotFilter;
import com.mynet.tableservice.response.TablesInfoResponse;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.tableservice.actions.ClearSideServiceAction;
import com.mynet.tableservice.actions.SitBotServiceAction;
import com.mynet.tableservice.actions.SitUserServiceAction;
import com.mynet.tableservice.actions.UpdatePotServiceAction;
import com.mynet.tableservice.service.ServiceTableModel;
import com.mynet.tableservice.service.TableService;
import com.mynet.tableservice.service.TableUpdateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UpdateTableProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(UpdateTableProcessor.class);
    private Gson gson;

    public UpdateTableProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

        String data = message.getData();
        TableUpdateWrapper wrapper = gson.fromJson(data, TableUpdateWrapper.class);
        ServiceTableModel table = TableService.getInstance().getTable(wrapper.getTableId());
        if(table == null){
            CacheController.getInstance().publishFixTableEvent(wrapper.getTableId());
        }else{
            switch (wrapper.getType()){
                case BOT_SIT:
                    sitBot(wrapper);
                    break;
                case POT_UPDATE:
                    updatePot(wrapper);
                    break;
                case USER_SIT:
                    sitTable(wrapper);
                    break;
                case CLEAR_SIDE:
                    clearSide(wrapper);
                    break;
            }
        }
    }

    private void clearSide(TableUpdateWrapper wrapper) {
        TableService service = TableService.getInstance();
        ServiceTableModel model = service.getTable(wrapper.getTableId());
        model.setSide(wrapper.getSide(), false);

        BasicUserModel user = wrapper.getUser();
        if(user != null){

            List<TableInfoModel> tables = service.getTablesInfo(user.getId(), user.getRoomId(), TablePairedFilter.ALL, TableRobotFilter.ALL, TableFullnessFilter.ALL);
            NetworkMessage message = new NetworkMessage(GameCommands.TABLES_INFO);
            message.setId(user.getId());
            message.setDataAsJSON(new TablesInfoResponse(tables, service.getRoomCount(user.getRoomId()), user.getRoomId()));

            service.sendNetworkMessage(user.getId(), user.getProxyId(), message);
        }
    }

    private void updatePot(TableUpdateWrapper wrapper) {
        ServiceTableModel model = TableService.getInstance().getTable(wrapper.getTableId());
        model.setPotValue(wrapper.getPot());
    }

    private void sitBot(TableUpdateWrapper wrapper) {
        ServiceTableModel model = TableService.getInstance().getTable(wrapper.getTableId());
        model.setSide(wrapper.getSide(), true);
    }

    private void sitTable(TableUpdateWrapper wrapper){
        ServiceTableModel model = TableService.getInstance().getTable(wrapper.getTableId());
        model.setSide(wrapper.getSide(), wrapper.getUser());
    }

}
