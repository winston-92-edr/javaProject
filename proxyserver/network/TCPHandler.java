package com.mynet.proxyserver.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.login.UserConnectionHandler;
import com.mynet.shared.launchers.ProxyServerLauncher;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.NodeController;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;


public class TCPHandler extends ChannelInboundHandlerAdapter implements UserConnectionHandler {
    final Logger logger = LoggerFactory.getLogger(TCPHandler.class);

    private ProxyUser user;
    private NodeController nodeController;

    public TCPHandler(NodeController nodeController) {
        this.nodeController = nodeController;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("NEW CONNECTION!!!!");
        ProxyServerLauncher.activeTCPConnections.incrementAndGet();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("CONNECTION LOST!!!!");
        ProxyServerLauncher.activeTCPConnections.decrementAndGet();
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

    private void handleIdle(ChannelHandlerContext ctx, ProxyUser user){
        if(user != null) {
            NetworkMessage networkMessage = new NetworkMessage(GameCommands.PING);

            //TODO: handleIdle response
            user.send(networkMessage);
            user.setLastPingTime(System.currentTimeMillis());
        }else{
            logger.error("User null closing connection..");
            ctx.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        NetworkMessage request = (NetworkMessage) msg;

        if (request != null) {
            if (user == null) {
                UserController.getInstance().login(request, ctx.channel(), this);
            } else {
                UserController.getInstance().processRequest(user, request);
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("CONNECTION ERROR: " + cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void setUser(ProxyUser user) {
        this.user = user;
    }

    public ProxyUser getUser() {
        return user;
    }
}
