package com.mynet.gameserver.response;

import com.mynet.gameserver.enums.KickType;
import com.mynet.gameserver.model.KickModel;

import java.util.List;

public class KickResponse {
    KickType type;
    KickModel data;

    public KickResponse(KickType type) {
        this.type = type;
    }

    public KickResponse(KickType type, KickModel data) {
        this.type = type;
        this.data = data;
    }
}
