package com.mynet.tableservice.response;
import com.mynet.gameserver.model.TableInfoModel;
import java.util.List;

public class TablesInfoResponse {
    List<TableInfoModel> tables;
    int count;
    int roomId;

    public TablesInfoResponse(List<TableInfoModel> tables,int count, int roomId) {
        this.tables = tables;
        this.count = count;
        this.roomId = roomId;
    }
}
