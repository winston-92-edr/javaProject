package com.mynet.shared.response;

import com.mynet.gameserver.enums.InfoCode;

public class InfoResponse {
    InfoCode code;
    String message;
    String url;
    boolean onTable;

    public InfoResponse(String message, InfoCode code) {
        this.code = code;
        this.message = message;
        this.onTable = true;
    }

    public InfoResponse(InfoCode code, String url, boolean onTable) {
        this.code = code;
        this.url = url;
        this.onTable = onTable;
    }
}
