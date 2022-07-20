package com.mynet.proxyserver.network.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import com.mynet.shared.network.NetworkMessage;

import java.util.List;

public class WebSocketOutHandler extends MessageToMessageEncoder<NetworkMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NetworkMessage o, List list) throws Exception {

        String msg = o.toJSON();
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(msg);
        list.add(textWebSocketFrame);

    }
}
