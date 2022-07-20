package com.mynet.shared.logs;

import com.mynet.shared.types.DataSourceType;

public class WinnerLogDbData extends QueueElement {

    private final WinnerDbLogType winnerDbLogType;
    private final String parameters;
    private final DataSourceType dataSourceType;

    public String getParameters() {
        return parameters;
    }

    public WinnerDbLogType getWinnerDbLogType() {
        return winnerDbLogType;
    }

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public WinnerLogDbData(WinnerDbLogType winnerDbLogType, String parameters, DataSourceType dataSourceType) {
        this.winnerDbLogType = winnerDbLogType;
        this.parameters = parameters;
        this.dataSourceType = dataSourceType;
    }
}
