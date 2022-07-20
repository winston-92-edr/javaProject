package com.mynet.proxyserver.observer;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.observer.ObserverRunnable;
import com.mynet.shared.db.generated.enums.NodesType;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;

public class MatchRequest extends ObserverRunnable {
    ProxyUser user;
    NetworkMessage request;
    String id;

    public MatchRequest() {
        this.id = "MatchRequest_" + System.currentTimeMillis();
    }

    @Override
    protected void setData(Object data) {
        RequestData message = (RequestData) data;
        this.user = message.user;
        this.request = message.request;
    }

    @Override
    protected String getId() {
        return this.id;
    }

    @Override
    public void run() {
        try {
            if(user.getServerType().equals(ServerType.GENERIC)) return;

            Node node = UserController.getInstance().getNodeController().getNode(NodesType.match);
            if (node != null) {
                request.setId(user.getId());
                node.sendRequest(request, user);
            } else {
                NetworkMessage message = new NetworkMessage(GameCommands.ERROR);
                message.setDataAsJSON(new ErrorResponse("Şu an sunucularımızda bakım çalışması yapılmaktadır.\nDaha sonra tekrar deneyiniz..", ErrorCode.NODE_NULL));
                user.send(message);
            }
        } catch (Exception e) {

        }
    }
}
