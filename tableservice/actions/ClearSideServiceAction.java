package com.mynet.tableservice.actions;

import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.tableservice.service.ServiceTableModel;
import com.mynet.tableservice.service.TableService;
import com.mynet.tableservice.service.TableUpdateWrapper;

public class ClearSideServiceAction extends AbstractServiceAction{
    private TableUpdateWrapper wrapper;

    public ClearSideServiceAction(TableUpdateWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void process() {
//        TableService service = TableService.getInstance();
//        ServiceTableModel model = service.getTable(wrapper.getTableId());
//        model.setSide(wrapper.getSide(), false);
//
//        BasicUserModel user = wrapper.getUser();
//        if(user != null){
//            String tablesInfo = service.getTablesInfo(user.getId(), user.getRoomId());
//            NetworkMessage message = new NetworkMessage(GameCommands.SET_TABLES_INFO);
//            message.setId(user.getId());
//            message.setData(tablesInfo);
//
//            service.sendNetworkMessage(user.getId(), user.getProxyId(), message);
//        }
    }
}
