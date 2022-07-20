package com.mynet.proxyserver.network.websocket;

import com.google.gson.stream.JsonReader;
import com.mynet.proxyserver.login.UserConnectionHandler;
import com.mynet.shared.resource.CacheController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.launchers.ProxyServerLauncher;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.NodeController;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;

import java.io.StringReader;

public class WebSocketHandler extends ChannelInboundHandlerAdapter implements UserConnectionHandler {
    Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    private ProxyUser user;
    private NodeController nodeController;

    public WebSocketHandler(NodeController nodeController) {
        this.nodeController = nodeController;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ProxyServerLauncher.activeWebSocketConnections.incrementAndGet();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ProxyServerLauncher.activeWebSocketConnections.decrementAndGet();
        ctx.channel().close();
        ctx.close();
        if(user != null) {
            user.setChannel(null);
            user.setLastConnectionLostTime(System.currentTimeMillis());
            nodeController.notifyDisconnect(user);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                handleIdle(ctx, user);
            } else if (e.state() == IdleState.WRITER_IDLE) {
                handleIdle(ctx, user);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(WebSocketHandler.class.getName(), cause);
    }

    @Override
    public void setUser(ProxyUser user) {
        this.user = user;
    }

    private void handleIdle(ChannelHandlerContext ctx, ProxyUser user){
        if(user != null) {
            NetworkMessage response = new NetworkMessage(GameCommands.PING);
          //  user.send(response);
            user.setLastPingTime(System.currentTimeMillis());
        }else{
            logger.error("User null closing connection..");
            ctx.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        WebSocketFrame frame = (WebSocketFrame) msg;
        try {
            if (frame instanceof TextWebSocketFrame) {
                String input = ((TextWebSocketFrame) frame).text();

                JsonReader reader = new JsonReader(new StringReader(input));
                reader.setLenient(true);
                NetworkMessage  request = NetworkMessage.getGson().fromJson(reader, NetworkMessage.class);

                if (request != null) {
                    if (user == null) {
                        String loginData = request.getData();
                        if(loginData == null){
                            ctx.channel().close();
                            ctx.close();
                        }else {
                            UserController.getInstance().login(request, ctx.channel(), this);
                        }
                    } else {
                        UserController.getInstance().processRequest(user, request);
                    }
                }
            }
        }finally{
            ReferenceCountUtil.release(msg);
        }
    }

}
