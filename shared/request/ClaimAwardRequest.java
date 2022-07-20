package com.mynet.shared.request;

import com.mynet.shared.types.ClaimAwardType;

public class ClaimAwardRequest {
    private int awardId;
    private ClaimAwardType awardType;

    public int getAwardId() {
        return awardId;
    }

    public ClaimAwardType getType() {
        return awardType;
    }
}
