package com.mynet.proxyserver.observer;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.observer.ObserverRunnable;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import com.mynet.tableservice.service.ServiceProxyDataWrapper;

public class TableRequest extends ObserverRunnable {
    ProxyUser user;
    NetworkMessage request;
    String id;

    public TableRequest() {
        this.id = "TableRequest_" + System.currentTimeMillis();
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
        if(user.getServerType().equals(ServerType.TOURNAMENT)) return;

        Node node = UserController.getInstance().getNodeController().getTableNode();
        if (node != null) {
            request.setId(user.getId());
            ServiceProxyDataWrapper wrapper = new ServiceProxyDataWrapper(request.getData(), user.getProxyID());
            String json = NetworkMessage.getGson().toJson(wrapper);
            request.setData(json);
            node.sendRequest(request, user);
        } else {
            NetworkMessage message = new NetworkMessage(GameCommands.ERROR);
            message.setDataAsJSON(new ErrorResponse("Şu an sunucularımızda bakım çalışması yapılmaktadır.\nDaha sonra tekrar deneyiniz..", ErrorCode.NODE_NULL));
            user.send(message);
        }
    }
}
