package com.mynet.tableservice.response;

import com.mynet.tableservice.model.QuickPlayRoomModel;

import java.util.List;

public class GetQuickPlayRoomsResponse {
    private final List<QuickPlayRoomModel> rooms;

    public GetQuickPlayRoomsResponse(List<QuickPlayRoomModel> rooms) {
        this.rooms = rooms;
    }
}
