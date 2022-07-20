package com.mynet.gameserver.response;
import java.util.ArrayList;

public class UserGiftsResponse {
    ArrayList<String> gifts;

    public UserGiftsResponse(ArrayList<String> gifts) {
        this.gifts = gifts;
    }
}
