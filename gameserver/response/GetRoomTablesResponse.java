package com.mynet.gameserver.response;

public class GetRoomTablesResponse {
    private boolean lock;
    private String tablesInfo;

    public GetRoomTablesResponse(boolean lock, String tablesInfo) {
        this.lock = lock;
        this.tablesInfo = tablesInfo;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getTablesInfo() {
        return tablesInfo;
    }

    public void setTablesInfo(String tablesInfo) {
        this.tablesInfo = tablesInfo;
    }
}
