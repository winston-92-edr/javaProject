package com.mynet.tableservice.actions;

import com.mynet.tableservice.service.ServiceTableModel;
import com.mynet.tableservice.service.TableService;

public class AddTableAction extends AbstractServiceAction{
    ServiceTableModel model;

    public AddTableAction(ServiceTableModel model) {
        this.model = model;
    }

    @Override
    public void process() {
        TableService.getInstance().addTable(model);
    }
}
