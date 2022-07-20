package com.mynet.tableservice.actions;

import com.mynet.tableservice.service.ServiceTableModel;
import com.mynet.tableservice.service.TableService;
import com.mynet.tableservice.service.TableUpdateWrapper;

public class SitUserServiceAction extends AbstractServiceAction{
    private TableUpdateWrapper wrapper;

    public SitUserServiceAction(TableUpdateWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void process() {
        ServiceTableModel model = TableService.getInstance().getTable(wrapper.getTableId());
        model.setSide(wrapper.getSide(), wrapper.getUser());
    }
}
