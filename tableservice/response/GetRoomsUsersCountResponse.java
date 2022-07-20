package com.mynet.tableservice.response;

import com.mynet.socialserver.model.RoomUserCountModel;

import java.util.ArrayList;
import java.util.HashMap;

public class GetRoomsUsersCountResponse {
    ArrayList<RoomUserCountModel> counts;

    public GetRoomsUsersCountResponse(ArrayList<RoomUserCountModel> counts) {
        this.counts = counts;
    }
}
