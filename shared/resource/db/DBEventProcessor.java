package com.mynet.shared.resource.db;

import com.mynet.shared.model.ServerEventModel;

public interface DBEventProcessor {
    void process(ServerEventModel event);
}
